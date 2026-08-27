import { useEffect, useState } from "react";
import { getProjects, createProjects } from "./api";

export default function ProjectList({ token, onSelect }) {
    const [projects, setProjects] = useState(null);
    const [error, setError] = useState(null);
    const [name, setName] = useState("");
    const [budget, setBudget] = useState("");
    const [creating, setCreating] = useState(false);

    function load() {
        setProjects(null);

        getProjects(token)
            .then((page) => setProjects(page.content))
            .catch((err) => setError(err.message));
    }

    useEffect(load, [token]);

    async function handleCreate(e) {
        e.preventDefault();
        setCreating(true);
        setError(null);

        try {
            await createProjects({ name, budget: Number(budget) }, token);
            setName("");
            setBudget("");
            load();
        } catch (err) {
            setError(err.message);
        } finally {
            setCreating(false);
        }
    }

    return (
        <div>
            <h1>Projects</h1>

            <form onSubmit={handleCreate}>
                <input placeholder="Project name" value={name} onChange={(e) => setName(e.target.value)} required />
                <input type="number" placeholder="Budget" value={budget} onChange={(e) => setBudget(e.target.value)} />
                <button type="submit" disabled={creating}>
                    {creating ? "Creating..." : "Create project"}
                </button>
            </form>

            {error && <p style={{ color: "red" }}>{error}</p>}

            {!projects ? (
                <p>Waking up the server, this can take up to a minute...</p>
            ) : projects.length === 0 ? (
                <p>No projects yet.</p>
            ) : (
                <ul>
                    {projects.map((project) => (
                        <li key={project.id}>
                            {project.name} 
                            <button onClick={() => onSelect(project)}>
                                View backlog
                            </button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}