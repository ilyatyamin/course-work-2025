import React, {useState} from 'react';

const BACKEND_URL = 'http://localhost:8080/api';

function Report({authToken}) {
    const [contestId, setContestId] = useState('');
    const [participants, setParticipants] = useState('');
    const [deadline, setDeadline] = useState('');
    const [yandexKey, setYandexKey] = useState('');
    const [isPlagiatCheckNeeded, setIsPlagiatCheckNeeded] = useState(false);
    const [mossKey, setMossKey] = useState('');
    const [saveFormat, setSaveFormat] = useState('PDF');
    const [, setReportData] = useState(null);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setReportData(null);

        try {
            let dateIso;
            try {
                dateIso = new Date(deadline).toISOString()
                // eslint-disable-next-line no-unused-vars
            } catch (e) {
                dateIso = null
            }

            const response = await fetch(`${BACKEND_URL}/report`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify({
                    contestId,
                    participants: participants.split(',').map(p => p.trim()),
                    dateIso,
                    yandexKey,
                    isPlagiatCheckNeeded,
                    mossKey,
                    saveFormat,
                }),
            });

            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `report.${saveFormat.toLowerCase()}`;
                document.body.appendChild(a);
                a.click();
                a.remove();
            } else {
                setError(`Failed to get report: ${response.statusText}`);
                const errorData = await response.json();
                console.error(errorData);
            }
        } catch (error) {
            setError(`An error occurred while fetching report: ${error.message}`);
            console.error(error);
        }
    };

    return (
        <div className="report-container">
            <h2>Get Contest Report</h2>
            {error && <p className="error-message">{error}</p>}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="contestId">Contest ID:</label>
                    <input
                        type="text"
                        id="contestId"
                        value={contestId}
                        onChange={(e) => setContestId(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="participants">Participants (comma-separated):</label>
                    <input
                        type="text"
                        id="participants"
                        value={participants}
                        onChange={(e) => setParticipants(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="deadline">Deadline:</label>
                    <input
                        type="datetime-local"
                        id="deadline"
                        value={deadline}
                        onChange={(e) => {
                            setDeadline(e.target.value)
                        }}
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="yandexKey">Yandex Key:</label>
                    <input
                        type="text"
                        id="yandexKey"
                        value={yandexKey}
                        onChange={(e) => setYandexKey(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="isPlagiatCheckNeeded">Check for Plagiarism:</label>
                    <input
                        type="checkbox"
                        id="isPlagiatCheckNeeded"
                        checked={isPlagiatCheckNeeded}
                        onChange={(e) => setIsPlagiatCheckNeeded(e.target.checked)}
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="mossKey">MOSS Key:</label>
                    <input
                        type="text"
                        id="mossKey"
                        value={mossKey}
                        onChange={(e) => setMossKey(e.target.value)}
                        disabled={!isPlagiatCheckNeeded}
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="saveFormat">Save Format:</label>
                    <select
                        id="saveFormat"
                        value={saveFormat}
                        onChange={(e) => setSaveFormat(e.target.value)}
                    >
                        <option value="PDF">PDF</option>
                        <option value="MD">MD</option>
                    </select>
                </div>
                <button type="submit" className="auth-button">Get Report</button>
            </form>

        </div>
    );
}

export default Report;