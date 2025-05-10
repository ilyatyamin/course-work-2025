import React from 'react';
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';

import {ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import LoginPage from './components/pages/LoginPage.jsx';
import ReportPage from './components/pages/ReportPage.jsx';
import AutoUpdatePage from './components/pages/AutoUpdate.jsx';
import ProtectedRoute from './routes/ProtectedRoute';
import Navbar from './components/providers/Navbar.jsx';
import './App.css';
import RegisterPage from "./components/pages/RegisterPage.jsx";
import MainPage from "./components/pages/Main.jsx";

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
            path="/register"
            element={<RegisterPage/>}
        />

        <Route
            path="/"
            element={
                <ProtectedRoute>
                    <LayoutWithNavbar>
                        <MainPage/>
                    </LayoutWithNavbar>
                </ProtectedRoute>
            }
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
            <AppRoutes/>
        </Router>
    );
}

export default App;
