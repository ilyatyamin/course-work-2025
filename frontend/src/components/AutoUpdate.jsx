import React, {useState} from 'react';
import {authFetch} from '../utils/authFetch';
import {handleBusinessError} from "../utils/errors.js";
import {formatDateForBackend} from "../utils/datetimeTools.js";

const AutoUpdatePage = () => {
    const [formData, setFormData] = useState({
        contestId: '',
        participants: '',
        deadline: '',
        yandexKey: '',
        credentialsGoogle: '',
        spreadsheetUrl: '',
        sheetName: '',
        cronExpression: '',
        autoUpdateId: ''
    });

    const [message, setMessage] = useState('');

    const handleChange = (e) => {
        setFormData(prev => ({...prev, [e.target.name]: e.target.value}));
    };

    const handleSetup = async (e) => {
        e.preventDefault();
        try {
            const deadlineValue = formData.deadline === '' ? null : formatDateForBackend(formData.deadline);
            const payload = {
                contestId: formData.contestId,
                participants: formData.participants.split(',').map(p => p.trim()),
                deadline: deadlineValue,
                yandexKey: formData.yandexKey,
                credentialsGoogle: formData.credentialsGoogle,
                spreadsheetUrl: formData.spreadsheetUrl,
                sheetName: formData.sheetName,
                cronExpression: formData.cronExpression
            };

            const res = await authFetch('http://localhost:8080/api/autoupdate', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                const data = await res.json();
                setMessage(`Автообновление установлено! ID: ${data.id}`);
                setFormData(prev => ({...prev, autoUpdateId: data.id.toString()}));
            } else {
                await handleBusinessError(res)
            }
        } catch (err) {
            await handleBusinessError(err)
        }
    };

    const handleRemove = async (e) => {
        e.preventDefault();
        if (!formData.autoUpdateId) {
            setMessage('Введите ID автообновления для удаления');
            return;
        }

        try {
            const res = await authFetch('http://localhost:8080/api/autoupdate', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    id: parseInt(formData.autoUpdateId)
                })
            });

            if (res.ok) {
                setMessage('Автообновление удалено!');
                setFormData(prev => ({
                    ...prev,
                    autoUpdateId: ''
                }));
            } else {
                await handleBusinessError(res)
            }
        } catch (err) {
            await handleBusinessError(err)
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-3xl mx-auto">
                <div className="bg-white shadow rounded-lg p-8">
                    <h1 className="text-3xl font-bold text-gray-900 mb-8 text-center">
                        Настройка автообновления
                    </h1>

                    <form className="space-y-6">
                        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
                            <div>
                                <label htmlFor="contestId" className="block text-sm font-medium text-gray-700">
                                    Contest ID
                                </label>
                                <input
                                    type="text"
                                    id="contestId"
                                    name="contestId"
                                    value={formData.contestId}
                                    onChange={handleChange}
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                                />
                            </div>

                            <div>
                                <label htmlFor="participants" className="block text-sm font-medium text-gray-700">
                                    Участники, каждый с новой строки
                                </label>
                                <textarea
                                    id="participants"
                                    name="participants"
                                    value={formData.participants}
                                    onChange={handleChange}
                                    placeholder="Оставьте пустым, чтобы построить отчет для всех участников соревнования"
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border w-full"
                                />
                            </div>

                            <div>
                                <label htmlFor="deadline" className="block text-sm font-medium text-gray-700">
                                    Дедлайн
                                </label>
                                <input
                                    type="datetime-local"
                                    id="deadline"
                                    name="deadline"
                                    value={formData.deadline}
                                    onChange={handleChange}
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                                />
                            </div>

                            <div>
                                <label htmlFor="yandexKey" className="block text-sm font-medium text-gray-700">
                                    Yandex API key
                                </label>
                                <input
                                    type="text"
                                    id="yandexKey"
                                    name="yandexKey"
                                    value={formData.yandexKey}
                                    onChange={handleChange}
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                                />
                            </div>

                            <div className="sm:col-span-2">
                                <label htmlFor="credentialsGoogle" className="block text-sm font-medium text-gray-700">
                                    Google JSON ключ
                                </label>
                                <textarea
                                    id="credentialsGoogle"
                                    name="credentialsGoogle"
                                    rows={3}
                                    value={formData.credentialsGoogle}
                                    onChange={handleChange}
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                                />
                            </div>

                            <div>
                                <label htmlFor="spreadsheetUrl" className="block text-sm font-medium text-gray-700">
                                    Ссылка на таблицу
                                </label>
                                <input
                                    type="text"
                                    id="spreadsheetUrl"
                                    name="spreadsheetUrl"
                                    value={formData.spreadsheetUrl}
                                    onChange={handleChange}
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                                />
                            </div>

                            <div>
                                <label htmlFor="sheetName" className="block text-sm font-medium text-gray-700">
                                    Имя листа
                                </label>
                                <input
                                    type="text"
                                    id="sheetName"
                                    name="sheetName"
                                    value={formData.sheetName}
                                    onChange={handleChange}
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                                />
                            </div>

                            <div>
                                <label htmlFor="cronExpression" className="block text-sm font-medium text-gray-700">
                                    Cron выражение
                                </label>
                                <input
                                    type="text"
                                    id="cronExpression"
                                    name="cronExpression"
                                    placeholder="0 0 * * * *"
                                    value={formData.cronExpression}
                                    onChange={handleChange}
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                                />
                            </div>

                            <div>
                                <label htmlFor="autoUpdateId" className="block text-sm font-medium text-gray-700">
                                    ID автообновления (для удаления)
                                </label>
                                <input
                                    type="text"
                                    id="autoUpdateId"
                                    name="autoUpdateId"
                                    value={formData.autoUpdateId}
                                    onChange={handleChange}
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                                />
                            </div>
                        </div>

                        {message && (
                            <div
                                className={`p-4 rounded-md ${message.includes('Ошибка') ? 'bg-red-50 text-red-700' : 'bg-green-50 text-green-700'}`}>
                                {message}
                            </div>
                        )}

                        <div className="flex justify-end space-x-4">
                            <button
                                onClick={handleRemove}
                                className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                            >
                                Удалить автообновление
                            </button>
                            <button
                                onClick={handleSetup}
                                className="px-4 py-2 bg-orange-600 text-white rounded-md hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
                            >
                                Установить автообновление
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default AutoUpdatePage;