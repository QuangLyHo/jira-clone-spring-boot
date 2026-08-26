import { useState } from "react";
import { createTask } from "./api";

export default function CreateTaskForm({ token, onCreated }) {
    const [title, setTitle] = useState("");
    const [status, setStatus] = useState("todo");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            await createTask({ title, status }, token);
            setTitle("");
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
            />
            <select value={status} onChange={(e) => setStatus(e.target.value)}>
                <option value="todo">To do</option>
                <option value="in_progress">In progress</option>
                <option value="done">Done</option>
            </select>
            <button type="submit" disabled={loading}>
                {loading ? "Adding..." : "Add task"}
            </button>
            {error && <p style={{ color: "red" }}>{error}</p>}
        </form>
    );
    
}