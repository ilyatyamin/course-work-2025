export async function authFetch(input, init = {}) {
    const url = typeof input === 'string' ? input : input.url;

    const isAuthRequest = url.includes('/api/login') || url.includes('/api/register');
    const token = localStorage.getItem('authToken');

    if (!isAuthRequest) {
        init.headers = {
            ...(init.headers || {}),
            Authorization: `Bearer ${token}`,
        };
    }

    let response = await fetch(input, init);
    if (response.status === 403 && !isAuthRequest) {
        const refreshToken = localStorage.getItem('refreshToken');
        const refreshResponse = await fetch('/api/login/refreshToken', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken }),
        });

        if (refreshResponse.ok) {
            const data = await refreshResponse.json();
            localStorage.setItem('authToken', data.authToken);
            localStorage.setItem('refreshToken', data.refreshToken);
            init.headers = {
                ...(init.headers || {}),
                Authorization: `Bearer ${data.authToken}`,
            };

            response = await fetch(input, init);
        } else {
            localStorage.clear();
            window.location.href = '/login';
        }
    }

    return response;
}
