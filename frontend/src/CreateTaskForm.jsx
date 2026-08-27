import { useState, useEffect } from "react";
import { createTask, getUsers } from "./api";

export default function CreateTaskForm({ token, projectId, onCreated }) {
    const [title, setTitle] = useState("");
    const [status, setStatus] = useState("todo");
    const [assigneeIds, setAssigneeIds] = useState([]);
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        getUsers(token)
            .then((page) => setUsers(page.content))
            .catch((err) => setError(err.message));
    }, [token]);

    function handleAssigneeChange(e) {
        const selected = Array.from(e.target.selectedOptions).map((opt) => Number(opt.value));
        setAssigneeIds(selected);
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            await createTask({ title, status, projectId, assigneeIds }, token);
            setTitle("");
            setAssigneeIds([]);
            onCreated();
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }
    return (
        <form onSubmit={handleSubmit}>
            <input 
                placeholder="Task title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
            />
            <select value={status} onChange={(e) => setStatus(e.target.value)}>
                <option value="todo">To do</option>
                <option value="in_progress">In progress</option>
                <option value="done">Done</option>
            </select>
            <select multiple value={assigneeIds} onChange={handleAssigneeChange}>
                {users.map((user) => (
                    <option key={user.id} value={user.id}>
                        {user.firstName} {user.lastName}
                    </option>
                ))}
            </select>
            <button type="submit" disabled={loading}>
                {loading ? "Adding..." : "Add task"}
            </button>
            {error && <p style={{ color: "red" }}>{error}</p>}
        </form>
    );
    
}