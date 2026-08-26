import { useEffect, useState } from "react";
import { getTasks } from "./api";

export default function TaskList({ token, refreshKey }) {
    const [tasks, setTasks] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        setTasks(null);
        getTasks(token)
            .then((page) => setTasks(page.content))
            .catch((err) => setError(err.message));
    }, [token, refreshKey]);

    if (error) return <p style={{ color: "red" }}>{error}</p>;
    if (!tasks) return <p>Waking up the server, this can take up to a minute...</p>;
    if (tasks.length === 0) return <p>No tasks yet.</p>

    return (
        <ul>
            {tasks.map((task) => (
                <li key={task.id} style={{ listStyleType: "none" }} >
                    {task.title} - {task.status}
                </li>
            ))}
        </ul>
    );
}