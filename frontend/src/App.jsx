import React, { useState } from 'react';
import Login from './components/Login';
import Register from './components/Register';
import Report from './components/ReportJson';
import './App.css';

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('authToken')); // Check for token on mount
    const [showRegister, setShowRegister] = useState(false);
    const authToken = localStorage.getItem('authToken'); // Get token

    const handleLoginSuccess = () => {
        setIsLoggedIn(true);
    };

    const handleRegisterSuccess = () => {
        setIsLoggedIn(true);
        setShowRegister(false); // Optionally switch back to login after registration
    };

    const handleLogout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('refreshToken');
        setIsLoggedIn(false);
    };

    return (
        <div className="app-container">
            <header className="app-header">
                <h1>YaContest Helper</h1>
            </header>

            <main className="app-main">
                {isLoggedIn ? (
                    <div>
                        <p>You are logged in!</p>
                        <button onClick={handleLogout} className="auth-button">Logout</button>
                        <Report authToken={authToken} />
                    </div>
                ) : (
                    <div>
                        {showRegister ? (
                            <Register onRegisterSuccess={handleRegisterSuccess} />
                        ) : (
                            <Login onLoginSuccess={handleLoginSuccess} />
                        )}
                        <button onClick={() => setShowRegister(!showRegister)} className="auth-button">
                            {showRegister ? 'Back to Login' : 'Register'}
                        </button>
                    </div>
                )}
            </main>

            <footer className="app-footer">
                <p>&copy; 2024 YaContest Helper</p>
            </footer>
        </div>
    );
}

export default App;