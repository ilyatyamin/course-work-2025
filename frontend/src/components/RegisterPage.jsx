import React from 'react';
import { useNavigate } from 'react-router-dom';
import {authFetch, setTokens} from '../utils/authFetch';
import { handleBusinessError } from '../utils/errors';

function Register() {
    const navigate = useNavigate();

    async function handleSubmit(event) {
        event.preventDefault();
        const form = event.target;

        try {
            const res = await authFetch('http://localhost:8080/api/register', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    username: form.username.value,
                    email: form.email.value,
                    password: form.password.value,
                    firstName: form.firstName.value || null,
                    lastName: form.lastName.value || null
                })
            });

            if (res.ok) {
                const data = await res.json();
                setTokens(data.authToken, data.refreshToken);
                navigate('/report');
            } else {
                await handleBusinessError(res);
            }
        } catch (err) {
            await handleBusinessError(err);
        }
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 to-orange-100 flex items-center justify-center p-4">
            <div className="w-full max-w-md">
                <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
                    <div className="p-8">
                        <h1 className="text-3xl font-bold text-center text-gray-800 mb-6">Регистрация</h1>
                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div>
                                <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-1">
                                    Имя пользователя
                                </label>
                                <input
                                    id="username"
                                    name="username"
                                    type="text"
                                    required
                                    className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-orange-500 focus:ring-2 focus:ring-orange-200 transition duration-200"
                                    placeholder="Введите логин"
                                />
                            </div>

                            <div>
                                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                                    Email
                                </label>
                                <input
                                    id="email"
                                    name="email"
                                    type="email"
                                    required
                                    className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-orange-500 focus:ring-2 focus:ring-orange-200 transition duration-200"
                                    placeholder="Введите email"
                                />
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-1">
                                        Имя
                                    </label>
                                    <input
                                        id="firstName"
                                        name="firstName"
                                        type="text"
                                        className="w-full px-4 py-3 rounded-lg border border-gray-300"
                                        placeholder="(необязательно)"
                                    />
                                </div>
                                <div>
                                    <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-1">
                                        Фамилия
                                    </label>
                                    <input
                                        id="lastName"
                                        name="lastName"
                                        type="text"
                                        className="w-full px-4 py-3 rounded-lg border border-gray-300"
                                        placeholder="(необязательно)"
                                    />
                                </div>
                            </div>

                            <div>
                                <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
                                    Пароль
                                </label>
                                <input
                                    id="password"
                                    name="password"
                                    type="password"
                                    required
                                    className="w-full px-4 py-3 rounded-lg border border-gray-300"
                                    placeholder="Введите пароль"
                                />
                            </div>

                            <button
                                type="submit"
                                className="w-full bg-orange-600 hover:bg-orange-700 text-white font-medium py-3 px-4 rounded-lg transition duration-200 shadow-md"
                            >
                                Зарегистрироваться
                            </button>
                        </form>

                        <div className="mt-6 text-center">
                            <a href="/login" className="text-orange-600 hover:text-orange-800 transition duration-200">
                                Уже есть аккаунт? Войти
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Register;
