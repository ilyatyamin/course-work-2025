import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {authFetch} from '../utils/authFetch';

function LoginPage() {
    const [isRegistering, setIsRegistering] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const toggleMode = () => {
        setIsRegistering(!isRegistering);
        setError('');
    };

    async function handleSubmit(event) {
        event.preventDefault();
        const form = event.target;
        const endpoint = isRegistering ? 'http://localhost:8080/api/register' : 'http://localhost:8080/api/login';

        try {
            const res = await authFetch(endpoint, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    username: form.username.value,
                    ...(isRegistering && {email: form.email.value}),
                    password: form.password.value
                })
            });

            if (res.ok) {
                const data = await res.json();
                localStorage.setItem('authToken', data.authToken);
                localStorage.setItem('refreshToken', data.refreshToken);
                navigate('/report');
            } else {
                const json = await res.json();
                setError(json.message || 'Произошла ошибка');
            }
        } catch (err) {
            setError('Не удалось подключиться к серверу');
        }
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
            <div className="w-full max-w-md">
                <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
                    <div className="p-8">
                        <div className="text-center mb-8">
                            <h1 className="text-3xl font-bold text-gray-800">
                                {isRegistering ? 'Создание аккаунта' : 'Вход в систему'}
                            </h1>
                            <p className="text-gray-500 mt-2">
                                {isRegistering ? 'Заполните форму для регистрации' : 'Введите свои данные для входа'}
                            </p>
                        </div>

                        {error && (
                            <div className="mb-6 bg-red-50 border-l-4 border-red-500 p-4 rounded">
                                <div className="flex items-center">
                                    <svg className="h-5 w-5 text-red-500 mr-3" fill="none" viewBox="0 0 24 24"
                                         stroke="currentColor">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                                              d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                                    </svg>
                                    <span className="text-red-700">{error}</span>
                                </div>
                            </div>
                        )}

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
                                    minLength={5}
                                    maxLength={50}
                                    className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200 transition duration-200"
                                    placeholder="Введите ваш логин"
                                />
                            </div>

                            {isRegistering && (
                                <div>
                                    <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                                        Email
                                    </label>
                                    <input
                                        id="email"
                                        name="email"
                                        type="email"
                                        required
                                        minLength={5}
                                        maxLength={255}
                                        className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200 transition duration-200"
                                        placeholder="Введите ваш email"
                                    />
                                </div>
                            )}

                            <div>
                                <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
                                    Пароль
                                </label>
                                <input
                                    id="password"
                                    name="password"
                                    type="password"
                                    required
                                    minLength={5}
                                    maxLength={255}
                                    className="w-full px-4 py-3 rounded-lg border border-gray-300 focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200 transition duration-200"
                                    placeholder="Введите ваш пароль"
                                />
                            </div>

                            <button
                                type="submit"
                                className="w-full bg-indigo-600 hover:bg-indigo-700 text-white font-medium py-3 px-4 rounded-lg transition duration-200 shadow-md"
                            >
                                {isRegistering ? 'Зарегистрироваться' : 'Войти'}
                            </button>
                        </form>

                        <div className="mt-6">
                            <div className="relative">
                                <div className="absolute inset-0 flex items-center">
                                    <div className="w-full border-t border-gray-300"></div>
                                </div>
                                <div className="relative flex justify-center text-sm">
                                    <span className="px-2 bg-white text-gray-500">
                                        Или
                                    </span>
                                </div>
                            </div>

                            <button
                                onClick={toggleMode}
                                className="w-full mt-6 text-indigo-600 hover:text-indigo-800 font-medium py-2 px-4 rounded-lg transition duration-200"
                            >
                                {isRegistering ? 'Уже есть аккаунт? Войти' : 'Нет аккаунта? Зарегистрироваться'}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default LoginPage;