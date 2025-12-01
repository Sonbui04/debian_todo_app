import React, { useState, useEffect } from "react";
import axios from "axios";
import "./App.css";

// API backend
const API_BASE = "http://localhost:8080/api";

function App() {
  // ================= AUTH STATE =================
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [authMode, setAuthMode] = useState("login");
  const [currentUser, setCurrentUser] = useState(null);
  const [message, setMessage] = useState("");

  // ================= TODO STATE =================
  const [todos, setTodos] = useState([]);
  const [newTitle, setNewTitle] = useState("");
  const [newDesc, setNewDesc] = useState("");
  const [editingId, setEditingId] = useState(null);
  const [fileToUpload, setFileToUpload] = useState(null);

  // Load todos when login success
  useEffect(() => {
    if (currentUser) loadTodos();
  }, [currentUser]);

  // ================= AUTH API =================
  const register = async () => {
    try {
      await axios.post(`${API_BASE}/auth/register`, { username, password });
      setMessage("Đăng ký thành công!");
      setAuthMode("login");
      setUsername("");
      setPassword("");
    } catch {
      setMessage("Tài khoản đã tồn tại.");
    }
  };

  const login = async () => {
    try {
      const res = await axios.post(`${API_BASE}/auth/login`, {
        username,
        password,
      });
      setCurrentUser(res.data);
      setMessage("");
    } catch {
      setMessage("Sai tài khoản hoặc mật khẩu.");
    }
  };

  const logout = () => {
    setCurrentUser(null);
    setTodos([]);
  };

  // ================= TODO API =================
  const loadTodos = async () => {
    const res = await axios.get(`${API_BASE}/todos/${currentUser.userId}`);
    setTodos(res.data);
  };

  const saveTodo = async () => {
    if (!newTitle.trim()) return;

    if (editingId) {
      await axios.put(`${API_BASE}/todos/${editingId}`, {
        userId: currentUser.userId,
        title: newTitle,
        description: newDesc,
      });
    } else {
      await axios.post(`${API_BASE}/todos`, {
        userId: currentUser.userId,
        title: newTitle,
        description: newDesc,
        done: false,
      });
    }

    setNewTitle("");
    setNewDesc("");
    setEditingId(null);
    loadTodos();
  };

  const toggleDone = async (todo) => {
    await axios.put(`${API_BASE}/todos/${todo.id}`, {
      ...todo,
      done: !todo.done,
    });
    loadTodos();
  };

  const deleteTodo = async (id) => {
    await axios.delete(`${API_BASE}/todos/${id}`);
    loadTodos();
  };

  const uploadAttachment = async (todoId) => {
    if (!fileToUpload) return;

    const formData = new FormData();
    formData.append("file", fileToUpload);

    await axios.post(`${API_BASE}/todos/${todoId}/attachment`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    setFileToUpload(null);
    loadTodos();
  };

  // ================= AUTH UI =================
  if (!currentUser) {
    return (
      <div className="auth-root">
        <div className="auth-card">
          <h2 className="auth-title">Todo App</h2>

          <div className="auth-tabs">
            <button
              className={authMode === "login" ? "active" : ""}
              onClick={() => setAuthMode("login")}
            >
              Login
            </button>
            <button
              className={authMode === "register" ? "active" : ""}
              onClick={() => setAuthMode("register")}
            >
              Signup
            </button>
          </div>

          {message && <p className="auth-error">{message}</p>}

          <input
            placeholder="Username"
            className="auth-input"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            placeholder="Password"
            type="password"
            className="auth-input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          {authMode === "login" ? (
            <button className="btn btn-primary" onClick={login}>
              Login
            </button>
          ) : (
            <button className="btn btn-primary" onClick={register}>
              Signup
            </button>
          )}
        </div>
      </div>
    );
  }

  // ================= MAIN UI =================
  return (
    <div className="app-root">
      <div className="app-card">
        <header className="app-header">
          <h1>Todo App</h1>

          <button className="btn btn-danger" onClick={logout}>
            Đăng xuất
          </button>
        </header>

        {/* FORM */}
        <section className="todo-form">
          <input
            className="app-input"
            placeholder="Tiêu đề công việc"
            value={newTitle}
            onChange={(e) => setNewTitle(e.target.value)}
          />
          <textarea
            className="app-input"
            placeholder="Mô tả"
            value={newDesc}
            onChange={(e) => setNewDesc(e.target.value)}
          ></textarea>

          <button className="btn btn-primary" onClick={saveTodo}>
            {editingId ? "Cập nhật" : "Thêm"}
          </button>
        </section>

        {/* TODO LIST */}
        <section>
          {todos.map((t) => (
            <div key={t.id} className="todo-item">
              <input
                type="checkbox"
                checked={t.done}
                onChange={() => toggleDone(t)}
              />

              <div className="todo-main">
                <div className={t.done ? "todo-title done" : "todo-title"}>
                  {t.title}
                </div>

                {t.description && (
                  <div className="todo-desc">{t.description}</div>
                )}

                {/* 🟦 FILE AREA */}
                {t.attachmentPath && (
                  <div className="todo-file">
                    📎 {t.attachmentPath.split("_").pop()}

                    <a
                      className="btn btn-secondary"
                      href={`http://localhost:8080${t.attachmentPath}`}
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      Xem / Tải xuống
                    </a>
                  </div>
                )}

                <div className="attach-wrap">
                  <input
                    type="file"
                    onChange={(e) => setFileToUpload(e.target.files[0])}
                  />
                  <button
                    className="btn btn-primary"
                    onClick={() => uploadAttachment(t.id)}
                  >
                    Attach
                  </button>
                </div>
              </div>

              <button className="btn btn-danger" onClick={() => deleteTodo(t.id)}>
                Xóa
              </button>
            </div>
          ))}
        </section>
      </div>
    </div>
  );
}

export default App;
