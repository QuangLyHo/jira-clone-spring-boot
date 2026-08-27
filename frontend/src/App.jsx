import { useEffect, useState } from "react";
import AuthPage from "./AuthPage";
import TaskList from "./TaskList";
import CreateTaskForm from "./CreateTaskForm";
import ProjectList from "./ProjectList";

function App() {
  const [token, setToken] = useState(() => localStorage.getItem("token"));
  const [selectedProject, setSelectedProject] = useState(null);
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    if (token) {
      localStorage.setItem("token", token);
    } else {
      localStorage.removeItem("token");
    }
  }, [token]);

  if (!token) {
    return <AuthPage onLogin={setToken} />
  }

  if (!selectedProject) {
    return <ProjectList token={token} onSelect={setSelectedProject} onLogout={() => setToken(null)} />;
  }

  return (
    <div>
      <button onClick={() => setSelectedProject(null)}>← Back to projects</button>
      <h1>{selectedProject.name}</h1>

      <CreateTaskForm 
        token={token} 
        projectId={selectedProject.id}
        onCreated={() => setRefreshKey((k) => k + 1)} />
      <TaskList 
        token={token} 
        projectId={selectedProject.id}
        refreshKey={refreshKey} />

      <button onClick={() => setToken(null)}>Log out</button>
    </div>
  );
}

export default App;