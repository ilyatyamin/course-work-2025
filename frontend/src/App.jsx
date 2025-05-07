import React from 'react';
import { BrowserRouter as Router, Navigate, Route, Routes } from 'react-router-dom';
import LoginPage from './components/LoginPage';
import ReportPage from './components/ReportPage';
import AutoUpdatePage from './components/AutoUpdate';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import './App.css'

const LayoutWithNavbar = ({ children }) => (
    <div>
        <Navbar />
        <div className="p-6">{children}</div>
    </div>
);

const AppRoutes = () => {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route
                path="/report"
                element={
                    <ProtectedRoute>
                        <LayoutWithNavbar>
                            <ReportPage />
                        </LayoutWithNavbar>
                    </ProtectedRoute>
                }
            />
            <Route
                path="/autoupdate"
                element={
                    <ProtectedRoute>
                        <LayoutWithNavbar>
                            <AutoUpdatePage />
                        </LayoutWithNavbar>
                    </ProtectedRoute>
                }
            />
            <Route path="*" element={<Navigate to="/report" />} />
        </Routes>
    );
};

function App() {
    return (
        <Router>
            <AppRoutes />
        </Router>
    );
}

export default App;
