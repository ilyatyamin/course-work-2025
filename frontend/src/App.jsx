import React from 'react';
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';
import LoginPage from './components/LoginPage';
import ReportPage from './components/ReportPage';
import AutoUpdatePage from './components/AutoUpdate';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/login" element={<LoginPage/>}/>
                <Route
                    path="/report"
                    element={<ProtectedRoute><ReportPage/></ProtectedRoute>}
                />
                <Route
                    path="/autoupdate"
                    element={<ProtectedRoute><AutoUpdatePage/></ProtectedRoute>}
                />
                <Route path="*" element={<Navigate to="/report"/>}/>
            </Routes>
        </Router>
    );
}

export default App;
