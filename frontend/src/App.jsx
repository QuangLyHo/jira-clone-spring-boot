import { useState } from "react";
import AuthPage from "./AuthPage";
import TaskList from "./TaskList";
import CreateTaskForm from "./CreateTaskForm";

function App() {
  const [token, setToken] = useState(null);
  const [refreshKey, setRefreshKey] = useState(0);

  if (!token) {
    return <AuthPage onLogin={setToken} />
  }

  return (
    <div>
      <h1>Tasks</h1>
      <CreateTaskForm token={token} onCreated={() => setRefreshKey((k) => k + 1)} />
      <TaskList token={token} refreshKey={refreshKey} />
      <button onClick={() => setToken(null)}>Log out</button>
    </div>
  );
}

export default App;