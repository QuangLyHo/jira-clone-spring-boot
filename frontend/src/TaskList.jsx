import { useEffect, useState } from "react";
import { getProjectTasks } from "./api";

export default function TaskList({ token, projectId, refreshKey }) {
    const [tasks, setTasks] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        setTasks(null);
        getProjectTasks(projectId, token)
            .then(setTasks)
            .catch((err) => setError(err.message));
    }, [token, projectId, refreshKey]);

    if (error) return <p style={{ color: "red" }}>{error}</p>;
    if (!tasks) return <p>Waking up the server, this can take up to a minute...</p>;
    if (tasks.length === 0) return <p>No tasks yet.</p>

    return (
        <ul>
            {tasks.map((task) => (
                <li key={task.id} style={{ listStyleType: "none" }} >
                    {task.title} - {task.status} - {
                        task.assignees.length === 0
                            ? "Unassigned"
                            : task.assignees.map((a) => `${a.firstName} ${a.lastName}`).join(", ")
                    }
                </li>
            ))}
        </ul>
    );
}