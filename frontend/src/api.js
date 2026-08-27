const API_URL = import.meta.env.VITE_API_URL;

async function request(path, { method = "GET", body, token } = {}) {
    const headers = { "Content-Type": "application/json" };
    if (token) headers.Authorization = `Bearer ${token}`;
    
    const response = await fetch(`${API_URL}${path}`, {
        method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
    });

    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        const message = data?.error ?? (data ? Object.values(data).join(", ") : `Request failed (${response.status})`);
        throw new Error(message);
    }

    return data;
}

export function login(body) {
    return request("/api/auth/login", { method: "POST", body });
}

export function register(body) {
    return request("/api/auth/register", { method: "POST", body });
}

export function getTasks(token) {
    return request("/api/tasks", { token });
}

export function createTask(body, token) {
    return request("/api/tasks", { method: "POST", body, token});
}

export function getProjects(token) {
    return request("/api/projects", { token });
}

export function createProject(body, token) {
    return request("/api/projects", { method: "POST", body, token });
}

export function getProjectTasks(projectId, token) {
    return request(`/api/projects/${projectId}/tasks`, { token });
}

export function getUsers(token) {
    return request("/api/users", { token });
}