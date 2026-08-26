const API_URL = import.meta.env.VITE_API_URL;

async function request(path, body) {
    const response = await fetch(`${API_URL}${path}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
    });

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        const message = data?.error ?? (data ? Object.values(data).join(", ") : `Request failed (${response.status})`);
        throw new Error(message);
    }

    return data;
}

export function login({ email, password }) {
    return request("/api/auth/login", { email, password });
}

export function register({ email, password, firstName, lastName }) {
    return request("/api/auth/register", { email, password, firstName, lastName });
}