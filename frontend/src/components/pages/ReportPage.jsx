import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {authFetch} from "../../utils/authFetch.js";
import {handleBusinessError} from "../../utils/errors.js";
import {formatDateForBackend} from "../../utils/datetimeTools.js";
import {finishLoading, FinishType, Jobs, startLoading} from "../../utils/loadingProcess.js";
import DataTable from '../../utils/tables.jsx'
import SaveKeyModal from "../keys/SaveKeyModal.jsx";


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

    const [tableId, setTableId] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [gradesData, setGradesData] = useState(null);

    const [showSaveModal, setShowSaveModal] = useState(false);
    const [keyToSave, setKeyToSave] = useState('');
    const [savedKeys, setSavedKeys] = useState([]);

    useEffect(() => {
        const username = localStorage.getItem("username");

        async function loadKeys() {
            const res = await authFetch("http://localhost:8080/api/user/secretKey", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({username, type: "YANDEX_CONTEST"}),
            });
            if (res.ok) {
                const data = await res.json();
                setSavedKeys(data.keys || []);
            }
        }

        loadKeys();
    }, []);


    const handleChange = (e) => {
        const {name, value, type, checked} = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');
        setGradesData('')
        setTableId('')

        const deadlineValue = formData.deadline === '' ? null : formatDateForBackend(formData.deadline);
        const spinnerId = startLoading(Jobs.REPORT)
        try {
            const payload = {
                contestId: formData.contestId,
                participantsList: formData.participantsList.split('\n').map((p) => p.trim()),
                deadline: deadlineValue,
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
                finishLoading(spinnerId)
                setTableId(data.tableId);
                setGradesData(data.results);

                if (shouldShowSaveModal(formData.yandexKey)) {
                    setKeyToSave(formData.yandexKey);
                    setShowSaveModal(true);
                }
            } else {
                await handleBusinessError(res, spinnerId)
            }
        } catch (err) {
            await handleBusinessError(err, spinnerId)
        } finally {
            setIsLoading(false);
        }
    };

    const downloadReport = async (format) => {
        setIsLoading(true);
        setError('');

        const spinnerId = startLoading(Jobs.REPORT_FILE)
        try {
            const deadlineValue = formData.deadline === '' ? null : formatDateForBackend(formData.deadline);
            const payload = {
                contestId: formData.contestId,
                participants: formData.participantsList.split('\n').map((p) => p.trim()),
                deadline: deadlineValue,
                yandexKey: formData.yandexKey,
                isPlagiatCheckNeeded: formData.plag,
                mossKey: formData.mossKey,
                saveFormat: format
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
                a.download = `report.${format.toLowerCase()}`;
                a.click();

                finishLoading(spinnerId);
            } else {
                await handleBusinessError(res, spinnerId)
            }
        } catch (err) {
            await handleBusinessError(err, spinnerId)
        } finally {
            setIsLoading(false);
        }
    };

    const downloadXLSX = async () => {
        setIsLoading(true);
        setError('');

        const spinnerId = startLoading(Jobs.REPORT_FILE)
        try {
            const res = await authFetch(`http://localhost:8080/api/grades/${tableId}/xlsx`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'}
            });

            if (res.ok) {
                const blob = await res.blob();
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'report.xlsx';
                a.click();

                finishLoading(spinnerId);
            } else {
                await handleBusinessError(res, spinnerId)
            }
        } catch (err) {
            await handleBusinessError(err, spinnerId)
        } finally {
            setIsLoading(false);
        }
    };

    const sendToSheets = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        const spinnerId = startLoading(Jobs.SHEETS_LOADING)
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
                setError('');
                finishLoading(spinnerId, FinishType.SUCCESS, "Успешно загружено в Гугл таблицы")
            } else {
                await handleBusinessError(res, spinnerId)
            }
        } catch (err) {
            await handleBusinessError(err, spinnerId)
        } finally {
            setIsLoading(false);
        }
    };

    const shouldShowSaveModal = (key) => {
        return !savedKeys.some(k => k.key === key);
    };


    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-4 py-6 sm:px-6 lg:px-8">
                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-2xl font-bold text-gray-900">Генерация отчета</h1>
                </div>

                <div className="bg-white shadow rounded-lg p-6 mb-6">
                    <form onSubmit={handleSubmit} className="space-y-6">

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
                                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                            />
                        </div>

                        <div>
                            <label htmlFor="participantsList" className="block text-sm font-medium text-gray-700">
                                Участники
                            </label>
                            <textarea
                                id="participantsList"
                                name="participantsList"
                                placeholder="Каждый с новой строки. Участники, каждый с новой строки. Оставьте пустым, чтобы построить отчет для всех участников соревнования"
                                value={formData.participantsList}
                                onChange={handleChange}
                                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
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
                                className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                            />
                        </div>

                        <div>
                            <label htmlFor="yandexKey" className="block text-sm font-medium text-gray-700">
                                Ключ к Yandex API
                            </label>
                            <div className="flex items-center gap-2">
                                <input
                                    id="yandexKey"
                                    name="yandexKey"
                                    type="text"
                                    value={formData.yandexKey}
                                    onChange={handleChange}
                                    placeholder="Введите ключ или выберите"
                                    className="flex-grow p-2 border rounded w-full"
                                    required
                                />
                                <select
                                    className="p-2 border rounded text-sm bg-gray-100"
                                    onChange={(e) => {
                                        const selectedId = parseInt(e.target.value);
                                        const selected = savedKeys.find(k => k.id === selectedId);
                                        if (selected) {
                                            setFormData((prev) => ({...prev, yandexKey: selected.key}));
                                        }
                                    }}
                                    defaultValue=""
                                    title="Выбрать сохранённый ключ"
                                >
                                    <option value="" disabled>🔑</option>
                                    {savedKeys.map((key) => (
                                        <option key={key.id} value={key.id}>
                                            {key.description}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        <div className="flex items-center">
                            <input
                                id="plag"
                                name="plag"
                                type="checkbox"
                                checked={formData.plag}
                                onChange={handleChange}
                                className="h-4 w-4 text-orange-600 focus:ring-orange-500 border-gray-300 rounded"
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
                                    className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                                />
                            </div>
                        )}

                        <div>
                            <button
                                type="submit"
                                disabled={isLoading}
                                className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-orange-600 hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500 disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                {isLoading ? 'Обработка...' : 'Сформировать'}
                            </button>
                        </div>
                    </form>

                    <SaveKeyModal
                        isOpen={showSaveModal}
                        onClose={() => setShowSaveModal(false)}
                        keyValue={keyToSave}
                        type="YANDEX_CONTEST"
                    />
                </div>

                {error && (
                    <div className="bg-red-50 border-l-4 border-red-500 p-4 mb-6">
                        <div className="flex">
                            <div className="flex-shrink-0">
                                <svg className="h-5 w-5 text-red-500" xmlns="http://www.w3.org/2000/svg"
                                     viewBox="0 0 20 20" fill="currentColor">
                                    <path fillRule="evenodd"
                                          d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                                          clipRule="evenodd"/>
                                </svg>
                            </div>
                            <div className="ml-3">
                                <p className="text-sm text-red-700">{error}</p>
                            </div>
                        </div>
                    </div>
                )}

                {gradesData && (
                    <div className="bg-white shadow rounded-lg p-6 mb-6 grid-cols-1 gap-4 sm:grid-cols-3">
                        <h2 className="text-lg font-medium text-gray-900 mb-4">Результаты</h2>
                        <button
                            onClick={downloadXLSX}
                            disabled={isLoading}
                            className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            Скачать баллы в XLSX
                        </button>

                        <button
                            onClick={() => downloadReport('PDF')}
                            disabled={isLoading}
                            className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-purple-600 hover:bg-purple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            Скачать детальный отчет в PDF
                        </button>

                        <button
                            onClick={() => downloadReport('MD')}
                            disabled={isLoading}
                            className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-amber-300 hover:bg-amber-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-500 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            Скачать детальный отчет в Markdown
                        </button>
                        <DataTable data={gradesData} transpose/>
                    </div>
                )}

                {tableId && (
                    <div className="bg-white shadow rounded-lg p-6">
                        <h2 className="text-lg font-medium text-gray-900 mb-4">Дополнительные действия</h2>
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
                                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
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
                                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
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
                                        className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-orange-500 focus:ring-orange-500 p-2 border"
                                    />
                                </div>

                                <button
                                    type="submit"
                                    disabled={isLoading}
                                    className="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-orange-600 hover:bg-orange-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-orange-500 disabled:opacity-50 disabled:cursor-not-allowed"
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