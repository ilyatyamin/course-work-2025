import React from 'react';
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';

import {ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import LoginPage from './components/LoginPage';
import ReportPage from './components/ReportPage';
import AutoUpdatePage from './components/AutoUpdate';
import ProtectedRoute from './routes/ProtectedRoute';
import Navbar from './components/Navbar';
import './App.css';

const LayoutWithNavbar = ({children}) => (
    <div>
        <Navbar/>
        <div className="p-6">{children}</div>
    </div>
);

const AppRoutes = () => (
    <Routes>
        <Route
            path="/login"
            element={<LoginPage/>}
        />

        <Route
            path="/report"
            element={
                <ProtectedRoute>
                    <LayoutWithNavbar>
                        <ReportPage/>
                    </LayoutWithNavbar>
                </ProtectedRoute>
            }
        />

        <Route
            path="/autoupdate"
            element={
                <ProtectedRoute>
                    <LayoutWithNavbar>
                        <AutoUpdatePage/>
                    </LayoutWithNavbar>
                </ProtectedRoute>
            }
        />

        {/* Редирект на /report по умолчанию */}
        <Route path="*" element={<Navigate to="/report" replace/>}/>
    </Routes>
);

function App() {
    return (
        <Router>
            <ToastContainer
                position="top-right"
                autoClose={4000}
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick
                rtl={false}
                pauseOnFocusLoss
                draggable
                pauseOnHover
                theme="light"
            />

            {/* Основные маршруты */}
            <AppRoutes/>
        </Router>
    );
}

export default App;
