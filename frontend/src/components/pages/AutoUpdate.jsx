import React, {useEffect, useState} from 'react';
import {authFetch} from '../../utils/authFetch.js';
import {handleBusinessError} from "../../utils/errors.js";
import {formatDateForBackend} from "../../utils/datetimeTools.js";
import SaveKeyModal from "../keys/SaveKeyModal.jsx";

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
    const [savedYandexKeys, setSavedYandexKeys] = useState([]);
    const [savedGoogleKeys, setSavedGoogleKeys] = useState([]);

    const [showSaveYandexModal, setShowSaveYandexModal] = useState(false);
    const [yandexKeyToSave, setYandexKeyToSave] = useState('');

    const [showSaveGoogleModal, setShowSaveGoogleModal] = useState(false);
    const [googleKeyToSave, setGoogleKeyToSave] = useState('');

    const [message, setMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        const username = localStorage.getItem("username");

        async function loadYaKeys() {
            const res = await authFetch("http://localhost:8080/api/user/secretKey", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({username, type: "YANDEX_CONTEST"}),
            });
            if (res.ok) {
                const data = await res.json();
                setSavedYandexKeys(data.keys || []);
            }
        }

        async function loadGoogleKeys() {
            const res = await authFetch("http://localhost:8080/api/user/secretKey", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({username, type: "GOOGLE_SHEETS"}),
            });
            if (res.ok) {
                const data = await res.json();
                setSavedGoogleKeys(data.keys || []);
            }
        }

        loadYaKeys();
        loadGoogleKeys()
    }, []);

    const handleChange = (e) => {
        setFormData(prev => ({...prev, [e.target.name]: e.target.value}));
    };

    const shouldShowSaveModal = (key, type) => {
        if (!key) return false;
        if (type === "YANDEX_CONTEST") {
            return !savedYandexKeys.some(k => k.key === key);
        }
        if (type === "GOOGLE_SHEETS") {
            return !savedGoogleKeys.some(k => k.key === key);
        }
        return false
    };

    const handleSetup = async (e) => {
        e.preventDefault();

        const showYandex = shouldShowSaveModal(formData.yandexKey, 'YANDEX_CONTEST');
        const showGoogle = shouldShowSaveModal(formData.credentialsGoogle, 'GOOGLE_SHEETS');

        if (showYandex) {
            setYandexKeyToSave(formData.yandexKey);
            setShowSaveYandexModal(true);
        }
        if (showGoogle) {
            setGoogleKeyToSave(formData.credentialsGoogle);
            setShowSaveGoogleModal(true);
        }

        try {
            setIsLoading(true);
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
        } finally {
            setIsLoading(false);
        }
    };

    const handleRemove = async (e) => {
        e.preventDefault();
        if (!formData.autoUpdateId) {
            setMessage('Введите ID автообновления для удаления');
            return;
        }

        try {
            setIsLoading(true);
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
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-7xl mx-auto px-4 py-6 sm:px-6 lg:px-8">
                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-3xl font-bold text-gray-900 mb-8 text-center">
                        Настройка автообновления
                    </h1>
                </div>
                <div className="bg-white shadow rounded-lg p-6 mb-6">
                    <form className="space-y-6">
                        <div>
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
                                    Участники
                                </label>
                                <textarea
                                    id="participants"
                                    name="participants"
                                    value={formData.participants}
                                    onChange={handleChange}
                                    placeholder="Каждый с новой строки. Оставьте пустым, чтобы построить отчет для всех участников соревнования"
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
                                    Ключ к Yandex API
                                </label>
                                <div className="flex items-center space-x-2">
                                    <input
                                        type="text"
                                        id="yandexKey"
                                        name="yandexKey"
                                        value={formData.yandexKey}
                                        onChange={handleChange}
                                        className="flex-grow p-2 border rounded w-full"
                                    />
                                    <select
                                        className="p-2 border rounded text-sm bg-gray-100"
                                        onChange={(e) => {
                                            const selectedId = parseInt(e.target.value);
                                            const selected = savedYandexKeys.find(k => k.id === selectedId);
                                            if (selected) {
                                                setFormData((prev) => ({...prev, yandexKey: selected.key}));
                                            }
                                        }}
                                        defaultValue=""
                                        title=""
                                    >
                                        <option value="" disabled>🔑</option>
                                        {savedYandexKeys.map((key) => (
                                            <option key={key.id} value={key.id}>
                                                {key.description}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>

                            <label htmlFor="credentialsGoogle" className="block text-sm font-medium text-gray-700">
                                Ключ к сервисному аккаунту Google. Прикрепите сюда JSON
                            </label>
                            <div className="flex items-center space-x-2">
                                <textarea
                                    id="credentialsGoogle"
                                    name="credentialsGoogle"
                                    rows={3}
                                    value={formData.credentialsGoogle}
                                    onChange={handleChange}
                                    className="flex-grow p-2 border rounded w-full"
                                    placeholder='{ "type": "service_account", "project_id":... '
                                />
                                <select
                                    className="p-2 border rounded text-sm bg-gray-100"
                                    onChange={(e) => {
                                        const selectedId = parseInt(e.target.value);
                                        const selected = savedGoogleKeys.find(k => k.id === selectedId);
                                        if (selected) {
                                            setFormData((prev) => ({...prev, credentialsGoogle: selected.key}));
                                        }
                                    }}
                                    defaultValue=""
                                    title="Выбрать"
                                >
                                    <option value="" disabled>🔑</option>
                                    {savedGoogleKeys.map((key) => (
                                        <option key={key.id} value={key.id}>
                                            {key.description}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div>
                                <label htmlFor="spreadsheetUrl" className="block text-sm font-medium text-gray-700">
                                    Ссылка на гугл таблицу
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
                                disabled={isLoading}
                            >
                                {isLoading ? 'Удаление...' : 'Удалить автообновление'}
                            </button>
                            <button
                                onClick={handleSetup}
                                className="px-4 py-2 bg-orange-600 text-white rounded-md hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500"
                                disabled={isLoading}
                            >
                                {isLoading ? 'Установка...' : 'Установить автообновление'}
                            </button>
                        </div>
                    </form>

                    <SaveKeyModal
                        isOpen={showSaveYandexModal}
                        onClose={() => setShowSaveYandexModal(false)}
                        keyValue={yandexKeyToSave}
                        type="YANDEX_CONTEST"
                    />

                    <SaveKeyModal
                        isOpen={showSaveGoogleModal}
                        onClose={() => setShowSaveGoogleModal(false)}
                        keyValue={googleKeyToSave}
                        type="GOOGLE_SHEETS"
                    />
                </div>
            </div>
        </div>
    );
};

export default AutoUpdatePage;
