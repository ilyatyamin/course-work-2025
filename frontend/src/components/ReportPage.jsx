// ReportPage.jsx
import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';

function ReportPage() {
    const [output, setOutput] = useState('');
    const [tableId, setTableId] = useState(null);
    const navigate = useNavigate();
    const token = localStorage.getItem('authToken');

    function logout() {
        localStorage.clear();
        navigate('/login');
    }

    async function handleSubmit(event) {
        event.preventDefault();
        const form = event.target;

        const payload = {
            contestId: form.contestId.value,
            participants: form.participants.value.split(',').map(p => p.trim()),
            deadline: form.deadline.value,
            yandexKey: form.yandexKey.value,
            isPlagiatCheckNeeded: form.plag.checked,
            mossKey: form.mossKey.value,
            saveFormat: form.format.value
        };

        const res = await fetch('/api/grades', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({withList: payload})
        });

        if (res.ok) {
            const data = await res.json();
            setOutput(JSON.stringify(data.results, null, 2));
            setTableId(data.tableId);
        } else {
            setOutput('Ошибка получения отчета');
        }
    }

    async function downloadXLSX() {
        const res = await fetch(`/api/grades/${tableId}/xlsx`, {
            method: 'POST',
            headers: {'Authorization': `Bearer ${token}`}
        });
        const blob = await res.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'report.xlsx';
        a.click();
    }

    async function sendToSheets(event) {
        event.preventDefault();
        const form = event.target;
        await fetch(`/api/grades/${tableId}/googleSheets`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                googleServiceAccountCredentials: form.creds.value,
                spreadsheetUrl: form.sheetUrl.value,
                listName: form.listName.value
            })
        });
        alert('Отправлено в Google Sheets');
    }

    return (
        <div className="container">
            <h1>Генерация отчета</h1>
            <button onClick={logout}>Выйти</button>
            <form onSubmit={handleSubmit}>
                <input name="contestId" placeholder="Contest ID" required/>
                <textarea name="participants" placeholder="Участники через запятую" required></textarea>
                <input name="deadline" type="datetime-local" required/>
                <input name="yandexKey" placeholder="Yandex API key" required/>
                <label><input type="checkbox" name="plag"/> Проверка на плагиат</label>
                <input name="mossKey" placeholder="MOSS key (если надо)"/>
                <select name="format">
                    <option value="PDF">PDF</option>
                    <option value="MD">Markdown</option>
                </select>
                <button type="submit">Сформировать</button>
            </form>

            <pre>{output}</pre>

            {tableId && (
                <>
                    <button onClick={downloadXLSX}>Скачать XLSX</button>
                    <form onSubmit={sendToSheets}>
                        <input name="creds" placeholder="Google JSON креды" required/>
                        <input name="sheetUrl" placeholder="Ссылка на таблицу" required/>
                        <input name="listName" placeholder="Имя листа" required/>
                        <button type="submit">Отправить в Google Sheets</button>
                    </form>
                </>
            )}
        </div>
    );
}

export default ReportPage;
