// LoginPage.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {authFetch} from "../utils/authFetch.js";

function LoginPage() {
    const [isRegistering, setIsRegistering] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const toggleMode = () => setIsRegistering(!isRegistering);

    async function handleSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const endpoint = isRegistering ? 'http://localhost:8080/api/register' : 'http://localhost:8080/api/login';
        const body = JSON.stringify({
            username: form.username.value,
            email: form.email?.value,
            password: form.password.value
        });

        const res = await authFetch(endpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body
        });

        if (res.ok) {
            const data = await res.json();
            localStorage.setItem('authToken', data.authToken);
            localStorage.setItem('refreshToken', data.refreshToken);
            navigate('/report');
        } else {
            setError('Ошибка входа/регистрации');
        }
    }

    return (
        <div className="container">
            <h1>{isRegistering ? 'Регистрация' : 'Вход'}</h1>
            <form onSubmit={handleSubmit}>
                <label>Имя пользователя</label>
                <input name="username" type="text" required />
                {isRegistering && (
                    <>
                        <label>Email</label>
                        <input name="email" type="email" required />
                    </>
                )}
                <label>Пароль</label>
                <input name="password" type="password" required />
                <button type="submit">{isRegistering ? 'Зарегистрироваться' : 'Войти'}</button>
            </form>
            <button onClick={toggleMode} style={{ marginTop: '10px' }}>
                {isRegistering ? 'Уже есть аккаунт? Войти' : 'Нет аккаунта? Зарегистрироваться'}
            </button>
            {error && <pre>{error}</pre>}
        </div>
    );
}

export default LoginPage;
