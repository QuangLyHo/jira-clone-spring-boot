import { useEffect, useState } from "react";
import { getProjects, createProject } from "./api";

export default function ProjectList({ token, onSelect, onLogout, onAuthError }) {
    const [projects, setProjects] = useState(null);
    const [error, setError] = useState(null);
    const [name, setName] = useState("");
    const [budget, setBudget] = useState("");
    const [creating, setCreating] = useState(false);

    function load() {
        setProjects(null);

        getProjects(token)
            .then((page) => setProjects(page.content))
            .catch((err) => {
                if (err.status === 401) {
                    onAuthError();
                } else  {
                    setError(err.message);
                }
            });
    }

    useEffect(load, [token]);

    async function handleCreate(e) {
        e.preventDefault();
        setCreating(true);
        setError(null);

        try {
            await createProject({ name, budget: Number(budget) }, token);
            setName("");
            setBudget("");
            load();
        } catch (err) {
            if (err.status === 401) {
                onAuthError();
            } else {
                setError(err.message);
            }
        } finally {
            setCreating(false);
        }
    }

    return (
        <div className="app-shell">
            <div className="topbar">
                <h1>Projects</h1>
                <button className="btn-secondary" onClick={onLogout}>Log out</button>
            </div>

            <div className="card">
                <form onSubmit={handleCreate} className="form-row">
                    <input placeholder="Project name" value={name} onChange={(e) => setName(e.target.value)} required />
                    <input type="number" placeholder="Budget" value={budget} onChange={(e) => setBudget(e.target.value)} />
                    <button type="submit" className="btn-primary" disabled={creating} style={{ flex: "none" }}>
                        {creating ? "Creating..." : "Create project"}
                    </button>
                </form>
            </div>

            {error && <p className="error-text">{error}</p>}

            {!projects ? (
                <p className="loading-text">Waking up the server, this can take up to a minute...</p>
            ) : projects.length === 0 ? (
                <p className="loading-text">No projects yet.</p>
            ) : (
                <ul className="list">
                    {projects.map((project) => (
                        <li key={project.id} className="list-item">
                            <div className="list-item-main">
                                <span className="list-item-title">{project.name}</span>
                                <span className="list-item-meta">Budget: ${project.budget}</span>
                            </div>
                            <button className="btn-secondary" onClick={() => onSelect(project)}>
                                View backlog
                            </button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}
