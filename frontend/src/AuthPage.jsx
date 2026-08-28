import { useState } from "react";
import { login, register } from "./api"

export default function AuthPage({ onLogin }) {
    const [mode, setMode] = useState("login");
    const [form, setForm] = useState({ email: "", password: "", firstName: "", lastName: "" });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    function handleChange(e) {
        setForm(prevForm => ({ ...prevForm, [e.target.name]: e.target.value }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            if (mode === "register") {
                await register(form);
            }
            const { token } = await login({ email: form.email, password: form.password });

            onLogin(token);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <div>
            <h1>{mode === "login" ? "Log in" : "Sign up"}</h1>

            <form onSubmit={handleSubmit}>
                {mode === "register" && (
                    <>
                        <input name="firstName" placeholder="First name" value={form.firstName} onChange={handleChange} required ></input>
                        <input name="lastName" placeholder="Last name" value={form.lastName} onChange={handleChange} required ></input>
                    </>
                )}
                <input name="email" type="email" placeholder="Email" value={form.email} onChange={handleChange} required></input>
                <input name="password" type="password" placeholder="Password" value={form.password} onChange={handleChange} required></input>

                <button type="submit" disabled={loading}>
                    {loading ? "Waking up the server, this can take up to a minute..." : mode === "login" ? "Log in" : "Sign up"}
                </button>
            </form>

            {error && <p style={{ color: "red" }}>{error}</p>}

            <button type="button" onClick={() => setMode(mode === "login" ? "register" : "login")}> 
                {mode === "login" ? "Need an account? Sign up" : "Already have an account? Log in"}
            </button>
        </div>
    );
}