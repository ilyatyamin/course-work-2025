import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {authFetch} from "../utils/authFetch.js";

function AutoUpdatePage() {
    const [message, setMessage] = useState('');
    const navigate = useNavigate();
    const token = localStorage.getItem('authToken');

    function logout() {
        localStorage.clear();
        navigate('/login');
    }

    async function handleAutoUpdate(event) {
        event.preventDefault();
        const form = event.target;

        const body = {
            contestId: form.contestId.value,
            participantsList: form.participants.value.split(',').map(p => p.trim()),
            deadline: form.deadline.value,
            yandexKey: form.yandexKey.value,
            credentialsGoogle: form.creds.value,
            spreadsheetUrl: form.sheetUrl.value,
            sheetName: form.sheetName.value,
            cronExpression: form.cron.value
        };

        const res = await authFetch('http://localhost:8080/api/update', {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(body)
        });

        if (res.ok) {
            const data = await res.json();
            setMessage(`Успешно установлено автообновление: id=${data.id}`);
        } else {
            setMessage('Ошибка при установке автообновления :(');
        }
    }

    async function handleDelete(event) {
        event.preventDefault();
        const id = event.target.id.value;

        const res = await authFetch('http://localhost:8080/api/update', {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({id: parseInt(id)})
        });

        if (res.ok) {
            setMessage('Удалено автообновление');
        } else {
            setMessage('Ошибка при удалении');
        }
    }

    return (
        <div className="container">
            <h1>Настройка автообновления</h1>
            <button onClick={logout}>Выйти</button>

            <form onSubmit={handleAutoUpdate}>
                <input name="contestId" placeholder="Contest ID" required/>
                <textarea name="participants" placeholder="Участники через запятую" required></textarea>
                <input name="deadline" type="datetime-local" required/>
                <input name="yandexKey" placeholder="Yandex API key" required/>
                <input name="creds" placeholder="Google JSON креды" required/>
                <input name="sheetUrl" placeholder="Ссылка на таблицу" required/>
                <input name="sheetName" placeholder="Имя листа" required/>
                <input name="cron" placeholder="Cron выражение" required/>
                <button type="submit">Установить автообновление</button>
            </form>

            <form onSubmit={handleDelete}>
                <input name="id" type="number" placeholder="ID автообновления для удаления" required/>
                <button type="submit">Удалить автообновление</button>
            </form>

            <pre>{message}</pre>
        </div>
    );
}

export default AutoUpdatePage;
