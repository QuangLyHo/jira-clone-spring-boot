import { useState } from "react";
import AuthPage from "./AuthPage";
import TaskList from "./TaskList";

function App() {
  const [token, setToken] = useState(null);

  if (!token) {
    return <AuthPage onLogin={setToken} />
  }

  return (
    <div>
      <h1>Tasks</h1>
      <TaskList token={token} />
      <button onClick={() => setToken(null)}>Log out</button>
    </div>
  );
}

export default App;