import React from 'react';
import { Navigate } from 'react-router-dom';

function PublicRoute({ children }) {
    const authToken = localStorage.getItem('authToken');
    const refreshToken = localStorage.getItem('refreshToken');

    const isAuthenticated = authToken && refreshToken;

    if (isAuthenticated) {
        return <Navigate to="/report" replace />;
    }

    return children;
}

export default PublicRoute;
