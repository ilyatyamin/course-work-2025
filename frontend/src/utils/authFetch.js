import {toast} from "react-toastify";
import {alertMessage} from "./errors.js";

const AUTH_TOKEN_KEY = 'authToken';
const REFRESH_TOKEN_KEY = 'refreshToken';
const REFRESH_TOKEN_ENDPOINT = 'http://localhost:8080/api/login/refreshToken';
const LOGIN_PAGE_URL = '/login';
const TOKEN_EXPIRED_MESSAGE = "Your token has expired. Please refresh it.";
const AUTH_HEADER_PREFIX = 'Bearer ';

export async function authFetch(input, init = {}) {
    const url = typeof input === 'string' ? input : input.url;
    const isAuthRequest = url.includes('/api/login') || url.includes('/api/register');

    if (!isAuthRequest) {
        const token = localStorage.getItem(AUTH_TOKEN_KEY);
        if (token) {
            init.headers = {
                ...(init.headers || {}),
                Authorization: `${AUTH_HEADER_PREFIX}${token}`,
            };
        }
    }

    let response = await fetch(input, init);
    const contentLength = response.headers.get('Content-Length');
    const bodyLength = parseInt(contentLength, 10);

    if (response.status === 401) {
        console.info("Got Unauthorized status for the request...");
        try {
            const responseBody = await response.clone().json();
            if (responseBody?.message === TOKEN_EXPIRED_MESSAGE) {
                console.info("Token expired, tried to refresh...");
                const newResponse = await attemptTokenRefresh(input, init);
                if (newResponse) {
                    return newResponse;
                }
            } else {
                console.info(`Got unknown message ${responseBody?.message}`);
            }
        } catch (error) {
            console.error("Error parsing response body or refreshing token:", error);
        }
        if (!isAuthRequest) {
            console.info(`Clear data and redirect user to login page`);
            clearAuthDataAndRedirectToLogin();
        }
    } else if (response.status === 403 && bodyLength === 0) {
        clearAuthDataAndRedirectToLogin();
        alertMessage("Вы не авторизованы. Авторизуйтесь заново");
    }
    return response;
}

async function attemptTokenRefresh(input, init) {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);

    if (!refreshToken) {
        console.warn("No refresh token found.");
        clearAuthDataAndRedirectToLogin();
        return null;
    }

    try {
        const refreshResponse = await fetch(REFRESH_TOKEN_ENDPOINT, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({refreshToken}),
        });

        if (refreshResponse.ok) {
            const data = await refreshResponse.json();
            localStorage.setItem(AUTH_TOKEN_KEY, data.authToken);
            localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken);

            init.headers = {
                ...(init.headers || {}),
                Authorization: `${AUTH_HEADER_PREFIX}${data.authToken}`,
            };

            return await fetch(input, init);
        } else {
            console.error("Token refresh failed:", refreshResponse.status, refreshResponse.statusText);
            clearAuthDataAndRedirectToLogin();
            return null;
        }
    } catch (error) {
        console.error("Error refreshing token:", error);
        clearAuthDataAndRedirectToLogin();
        return null;
    }
}

function clearAuthDataAndRedirectToLogin() {
    localStorage.clear();
    window.location.href = LOGIN_PAGE_URL;
}