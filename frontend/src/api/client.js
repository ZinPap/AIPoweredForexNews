

import axios from 'axios';

const API_BASE = 'http://localhost:8089/api';

export const api = axios.create({
    baseURL: API_BASE,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add userId to all requests (temporary until auth is implemented)
api.interceptors.request.use((config) => {
    if (!config.params) {
        config.params = {};
    }
    if (!config.params.userId) {
        config.params.userId = 1;
    }
    return config;
});

// Article endpoints
export const articleApi = {
    // Get articles with filters (category, type, source, impact, status, q, page, size)
    getArticles: (params) => api.get('/articles', { params }),

    // Get single article by ID
    getArticle: (id) => api.get(`/articles/${id}`),

    // Get unread count for user
    getUnreadCount: (userId) => api.get('/me/unread-count', { params: { userId } }),

    // Mark article as read
    markAsRead: (id, userId) => api.post(`/articles/${id}/read`, null, { params: { userId } }),

    // Mark article as unread
    markAsUnread: (id, userId) => api.delete(`/articles/${id}/read`, { params: { userId } }),

    // Generate AI summary
    generateSummary: (id, userId) => api.post(`/articles/${id}/summarize`, null, { params: { userId } }),

    // Get all distinct sources (for filters)
    getSources: () => api.get('/sources'),
};