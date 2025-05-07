import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authFetch } from "../utils/authFetch";

function ReportPage() {
    const [formData, setFormData] = useState({
        contestId: '',
        participantsList: '',
        deadline: '',
        yandexKey: '',
        plag: false,
        mossKey: '',
        format: 'PDF'
    });

    const [output, setOutput] = useState('');
    const [tableId, setTableId] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const formatDate = (dateStr) => {
        const date = new Date(dateStr);
        return `${date.getFullYear()}-${(date.getMonth() + 1)
            .toString()
            .padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(
            date.getHours()
        ).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(
            date.getSeconds()
        ).padStart(2, '0')}`;
    };

    const logout = () => {
        localStorage.clear();
        navigate('/login');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        try {
            const payload = {
                contestId: formData.contestId,
                participantsList: formData.participantsList.split(',').map((p) => p.trim()),
                deadline: formatDate(formData.deadline),
                yandexKey: formData.yandexKey
            };

            const res = await authFetch('http://localhost:8080/api/grades', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                const data = await res.json();
                setOutput(JSON.stringify(data.results, null, 2));
                setTableId(data.tableId);
            } else {
                const json = await res.json();
                setError(json.message || 'Произошла ошибка');
            }
        } catch (err) {
            setError('Не удалось подключиться к серверу');
        } finally {
            setIsLoading(false);
        }
    };

    const downloadReport = async () => {
        setIsLoading(true);
        setError('');

        try {
            const payload = {
                contestId: formData.contestId,
                participants: formData.participantsList.split(',').map((p) => p.trim()),
                deadline: formatDate(formData.deadline),
                yandexKey: formData.yandexKey,
                isPlagiatCheckNeeded: formData.plag,
                mossKey: formData.mossKey,
                saveFormat: formData.format
            };

            const res = await authFetch('http://localhost:8080/api/report', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            if (res.ok) {
                const blob = await res.blob();
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `report.${formData.format.toLowerCase()}`;
                a.click();
            } else {
                const json = await res.json();
                setError(json.message || 'Произошла ошибка');
            }
        } catch (err) {
            setError('Не удалось подключиться к серверу');
        } finally {
            setIsLoading(false);
        }
    };

    const downloadXLSX = async () => {
        setIsLoading(true);
        setError('');

        try {
            const res = await authFetch(`http://localhost:8080/api/grades/${tableId}/xlsx`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });

            if (res.ok) {
                const blob = await res.blob();
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'report.xlsx';
                a.click();
            } else {
                const json = await res.json();
                setError(json.message || 'Произошла ошибка');
            }
        } catch (err) {
            setError('Не удалось подключиться к серверу');
        } finally {
            setIsLoading(false);
        }
    };

    const sendToSheets = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        try {
            const form = e.target;
            const res = await authFetch(`http://localhost:8080/api/grades/${tableId}/googleSheets`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    googleServiceAccountCredentials: form.creds.value,
                    spreadsheetUrl: form.sheetUrl.value,
                    listName: form.listName.value
                })
            });

            if (res.ok) {
                setError(''); // Clear any previous errors
                alert('Отправлено в Google Sheets');
            } else {
                const json = await res.json();
                setError(json.message || 'Произошла ошибка');
            }
        } catch (err) {
            setError('Не удалось подключиться к серверу');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 py-6 sm:px-6 lg:px-8">
                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-2xl font-bold text-gray-900">Генерация отчета</h1>
                    <button
                        onClick={logout}
                        className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
                    >
                        Выйти
                    </button>
                </div>

                <div className="bg-white shadow rounded-lg p-6 mb-6">
                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
                            <div>
                                <label htmlFor="contestId" className="block text-sm font-medium text-gray-700">
                                    Contest ID
                                </label>
                                <input
                                    id="contestId"
                                    name="contestId"
                                    placeholder="Contest ID"
                                    value={formData.contestId}
                                    onChange={handleChange}
                                    required
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border"
                                />
                            </div>

                            <div>
                                <label htmlFor="participantsList" className="block text-sm font-medium text-gray-700">
                                    Участники (через запятую)
                                </label>
                                <textarea
                                    id="participantsList"
                                    name="participantsList"
                                    placeholder="Участники через запятую"
                                    value={formData.participantsList}
                                    onChange={handleChange}
                                    required
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border"
                                />
                            </div>

                            <div>
                                <label htmlFor="deadline" className="block text-sm font-medium text-gray-700">
                                    Дедлайн
                                </label>
                                <input
                                    id="deadline"
                                    name="deadline"
                                    type="datetime-local"
                                    value={formData.deadline}
                                    onChange={handleChange}
                                    required
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border"
                                />
                            </div>

                            <div>
                                <label htmlFor="yandexKey" className="block text-sm font-medium text-gray-700">
                                    Yandex API key
                                </label>
                                <input
                                    id="yandexKey"
                                    name="yandexKey"
                                    placeholder="Yandex API key"
                                    value={formData.yandexKey}
                                    onChange={handleChange}
                                    required
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border"
                                />
                            </div>

                            <div className="flex items-center">
                                <input
                                    id="plag"
                                    name="plag"
                                    type="checkbox"
                                    checked={formData.plag}
                                    onChange={handleChange}
                                    className="h-4 w-4 text-indigo-600 focus:ring-indigo-500 border-gray-300 rounded"
                                />
                                <label htmlFor="plag" className="ml-2 block text-sm text-gray-700">
                                    Проверка на плагиат
                                </label>
                            </div>

                            {formData.plag && (
                                <div>
                                    <label htmlFor="mossKey" className="block text-sm font-medium text-gray-700">
                                        MOSS key
                                    </label>
                                    <input
                                        id="mossKey"
                                        name="mossKey"
                                        placeholder="MOSS key (если надо)"
                                        value={formData.mossKey}
                                        onChange={handleChange}
                                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border"
                                    />
                                </div>
                            )}

                            <div>
                                <label htmlFor="format" className="block text-sm font-medium text-gray-700">
                                    Формат отчета
                                </label>
                                <select
                                    id="format"
                                    name="format"
                                    value={formData.format}
                                    onChange={handleChange}
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border"
                                >
                                    <option value="PDF">PDF</option>
                                    <option value="MD">Markdown</option>
                                </select>
                            </div>
                        </div>

                        <div>
                            <button
                                type="submit"
                                disabled={isLoading}
                                className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                {isLoading ? 'Обработка...' : 'Сформировать'}
                            </button>
                        </div>
                    </form>
                </div>

                {error && (
                    <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-6">
                        <div className="flex">
                            <div className="flex-shrink-0">
                                <svg className="h-5 w-5 text-red-500" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                                </svg>
                            </div>
                            <div className="ml-3">
                                <p className="text-sm text-red-700">{error}</p>
                            </div>
                        </div>
                    </div>
                )}

                {output && (
                    <div className="bg-white shadow rounded-lg p-6 mb-6">
                        <h2 className="text-lg font-medium text-gray-900 mb-4">Результаты</h2>
                        <pre className="whitespace-pre-wrap bg-gray-50 p-4 rounded overflow-x-auto text-sm">
                            {output}
                        </pre>
                    </div>
                )}

                {tableId && (
                    <div className="bg-white shadow rounded-lg p-6">
                        <h2 className="text-lg font-medium text-gray-900 mb-4">Дополнительные действия</h2>

                        <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
                            <button
                                onClick={downloadXLSX}
                                disabled={isLoading}
                                className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                Скачать XLSX
                            </button>

                            <button
                                onClick={downloadReport}
                                disabled={isLoading}
                                className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                Скачать отчет
                            </button>
                        </div>

                        <div className="mt-6">
                            <h3 className="text-md font-medium text-gray-900 mb-2">Отправить в Google Sheets</h3>
                            <form onSubmit={sendToSheets} className="space-y-4">
                                <div>
                                    <label htmlFor="creds" className="block text-sm font-medium text-gray-700">
                                        Google JSON креды
                                    </label>
                                    <textarea
                                        id="creds"
                                        name="creds"
                                        required
                                        rows={3}
                                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border"
                                    />
                                </div>

                                <div>
                                    <label htmlFor="sheetUrl" className="block text-sm font-medium text-gray-700">
                                        Ссылка на таблицу
                                    </label>
                                    <input
                                        id="sheetUrl"
                                        name="sheetUrl"
                                        required
                                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border"
                                    />
                                </div>

                                <div>
                                    <label htmlFor="listName" className="block text-sm font-medium text-gray-700">
                                        Имя листа
                                    </label>
                                    <input
                                        id="listName"
                                        name="listName"
                                        required
                                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 p-2 border"
                                    />
                                </div>

                                <button
                                    type="submit"
                                    disabled={isLoading}
                                    className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50 disabled:cursor-not-allowed"
                                >
                                    Отправить в Google Sheets
                                </button>
                            </form>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default ReportPage;