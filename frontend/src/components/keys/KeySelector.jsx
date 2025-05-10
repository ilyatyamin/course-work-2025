import React, { useEffect, useState } from "react";
import { authFetch } from "../../utils/authFetch.js";

const KeySelector = ({ type, onSelect }) => {
    const [keys, setKeys] = useState([]);
    const username = localStorage.getItem("username");

    useEffect(() => {
        async function fetchKeys() {
            const res = await authFetch("http://localhost:8080/api/user/secretKey", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, type })
            });
            if (res.ok) {
                const data = await res.json();
                setKeys(data.keys);
            }
        }
        fetchKeys();
    }, [type, username]);

    return (
        <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">
                Выберите сохранённый {type === "YANDEX_CONTEST" ? "Яндекс" : "Google"} ключ:
            </label>
            <select
                onChange={(e) => {
                    const selectedKey = keys.find(k => k.id === parseInt(e.target.value));
                    if (selectedKey) onSelect(selectedKey.key);
                }}
                className="input"
                defaultValue=""
            >
                <option value="" disabled>Выберите...</option>
                {keys.map(k => (
                    <option key={k.id} value={k.id}>
                        {k.description}
                    </option>
                ))}
            </select>
        </div>
    );
};

export default KeySelector;
