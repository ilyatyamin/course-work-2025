import React, { useEffect, useState } from "react";
import { authFetch } from "../utils/authFetch";
import { useNavigate } from "react-router-dom";
import { Loader2 } from "lucide-react";
import DataTable from "../utils/tables.jsx";

const Home = () => {
    const [user, setUser] = useState({ firstName: "", lastName: "" });
    const [tasks, setTasks] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        async function fetchData() {
            try {
                const username = localStorage.getItem("username");

                const payload = {'username' : username};

                const [userRes, updatesRes] = await Promise.all([
                    authFetch("http://localhost:8080/api/user", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(payload),
                    }),
                    authFetch("http://localhost:8080/api/user/autoupdate", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(payload),
                    }),
                ]);

                if (userRes.ok && updatesRes.ok) {
                    const userData = await userRes.json();
                    const updatesData = await updatesRes.json();

                    setUser({
                        firstName: userData.firstName || "",
                        lastName: userData.lastName || "",
                    });

                    const tasks = Object.fromEntries(
                        updatesData.tasks.map(({ id, ...rest }) => [id, rest])
                    );

                    console.info(tasks)

                    setTasks(tasks);
                } else {
                    navigate("/report");
                }
            } catch (err) {
                console.error("Ошибка при получении данных:", err);
                navigate("/report");
            } finally {
                setLoading(false);
            }
        }

        fetchData();
    }, [navigate]);

    if (loading) {
        return (
            <div className="flex justify-center items-center h-screen">
                <Loader2 className="animate-spin h-10 w-10 text-orange-400" />
            </div>
        );
    }

    const greeting = user.firstName
        ? `Привет, ${user.firstName}${user.lastName ? ` ${user.lastName}` : ""}!`
        : "Привет!";

    return (
        <div className="min-h-screen bg-gray-100 py-12 px-4">
            <div className="max-w-4xl mx-auto bg-white p-8 rounded-2xl shadow-lg space-y-8">
                <div className="flex justify-between items-center">
                    <h1 className="text-3xl font-bold text-gray-800">{greeting}</h1>
                </div>

                <div>
                    <h2 className="text-xl font-semibold text-gray-700 mb-4">
                        🔁 Ваши задачи на автообновление оценок
                    </h2>

                    {tasks.length === 0 ? (
                        <p className="text-gray-500">Пока нет задач автообновления.</p>
                    ) : (
                        <DataTable data={tasks} />
                    )}
                </div>
            </div>
        </div>
    );
};

export default Home;
