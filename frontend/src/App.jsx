import { useState } from "react";
import AuthPage from "./AuthPage";

function App() {
  const [token, setToken] = useState(null);

  if (!token) {
    return <AuthPage onLogin={setToken} />
  }

  return (
    <div>
      <h1>Logged in</h1>
      <p>You have a valid JWT. Backend connection confirmed.</p>
      <button onClick={() => setToken(null)}>Log out</button>
    </div>
  );
}

export default App;