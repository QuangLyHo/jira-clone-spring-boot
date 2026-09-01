import { useEffect, useState } from "react";
import { getProjectTasks, updateTaskStatus } from "./api";

export default function TaskList({ token, projectId, refreshKey, onAuthError }) {
    const [tasks, setTasks] = useState(null);
    const [error, setError] = useState(null);

    function load() {
        setTasks(null);
        getProjectTasks(projectId, token)
            .then(setTasks)
            .catch((err) => {
                if (err.status === 401) {
                    onAuthError();
                } else {
                    setError(err.message);
                }
            });
    }

    useEffect(load, [token, projectId, refreshKey]);

    async function handleStatusChange(taskId, newStatus) {
        try {
            await updateTaskStatus(taskId, newStatus, token);
            load();
        } catch (err) {
            if (err.status === 401) {
                onAuthError();
            } else {
                setError(err.message);
            }
        }
    }

    if (error) return <p style={{ color: "red" }}>{error}</p>;
    if (!tasks) return <p>Waking up the server, this can take up to a minute...</p>;
    if (tasks.length === 0) return <p>No tasks yet.</p>

    return (
        <ul>
            {tasks.map((task) => (
                <li key={task.id} style={{ listStyleType: "none" }} >
                    {task.title} - {
                        task.assignees.length === 0
                            ? "Unassigned"
                            : task.assignees.map((a) => `${a.firstName} ${a.lastName}`).join(", ")
                    }
                    <select value={task.status} onChange={(e) => 
                            handleStatusChange(task.id, e.target.value)}>

                        <option value="todo">To do</option>
                        <option value="in_progress">In progress</option>
                        <option value="done">Done</option>

                    </select>
                </li>
            ))}
        </ul>
    );
}