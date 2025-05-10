import React, {useState} from "react";
import {authFetch} from "../../utils/authFetch.js";
import {toast} from "react-toastify";
import {handleBusinessError} from "../../utils/errors.js";

const SaveKeyModal = ({isOpen, onClose, type, keyValue}) => {
    const [description, setDescription] = useState("");

    if (!isOpen) return null;

    const handleSave = async () => {
        const username = localStorage.getItem("username");
        const res = await authFetch("http://localhost:8080/api/user/secretKey", {
            method: "PUT",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                username,
                type,
                key: keyValue,
                description
            })
        });

        if (res.ok) {
            toast.info("Ключ успешно сохранён")
            onClose();
        } else {
            handleBusinessError(res);
        }
    };

    function getHeader() {
        switch (type) {
            case "GOOGLE_SHEETS":
                return "Хотите сохранить сервисный ключ от гугл таблиц в память, чтобы в дальнейшем его не вводить?"
            case "YANDEX_CONTEST":
                return "Хотите сохранить API ключ от Яндекс Контеста в память, чтобы в дальнейшем его не вводить?"
            default:
                return "Неверный тип"
        }
    }

    return (
        <div className="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50">
            <div className="bg-white rounded-xl shadow-xl p-6 w-full max-w-md">
                <h2 className="text-xl font-bold text-gray-800 mb-4">Сохранить ключ</h2>
                <p className="text-gray-600 mb-2">
                    {getHeader()}
                </p>

                <label className="block mb-2 text-sm font-medium text-gray-700">Описание:</label>
                <input
                    type="text"
                    className="input w-fit mb-4"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    required
                    placeholder="например: ключ для описания борзой"
                />

                <div className="flex justify-end gap-2">
                    <button
                        onClick={onClose}
                        className="bg-gray-200 hover:bg-gray-300 text-gray-800 px-4 py-2 rounded-lg"
                    >
                        Отмена
                    </button>
                    <button
                        onClick={handleSave}
                        className="bg-orange-300 hover:bg-orange-500 text-white px-4 py-2 rounded-lg"
                    >
                        Сохранить
                    </button>
                </div>
            </div>
        </div>
    );
};

export default SaveKeyModal;
