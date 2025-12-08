// // API Configuration
// const API_CONFIG = {
//     BASE_URL: 'https://fitness-app-0zk0.onrender.com/api',
//     ENDPOINTS: {
//         // Authentication
//         LOGIN: '/api/auth/login',
//         REGISTER: '/api/auth/register',
//         LOGOUT: '/api/auth/logout',
//         REFRESH: '/api/auth/refresh',
//         RESET_PASSWORD: '/api/auth/reset-password',

//         // Members
//         MEMBERS: '/api/members',
//         MEMBER_BY_ID: (id) => `/api/members/${id}`,

//         // Classes
//         CLASSES: '/api/classes',
//         CLASS_BY_ID: (id) => `/api/classes/${id}`,

//         // Bookings
//         BOOKINGS: '/api/bookings',
//         BOOKING_BY_ID: (id) => `/api/bookings/${id}`,

//         // Payments
//         PAYMENTS: '/api/payments',
//         PAYMENT_BY_ID: (id) => `/api/payments/${id}`,

//         // Trainers
//         TRAINERS: '/api/trainers',
//         TRAINER_BY_ID: (id) => `/api/trainers/${id}`,

//         // Contact
//         CONTACT: '/api/contact-messages',

//         // Dashboard
//         DASHBOARD_OVERVIEW: '/api/dashboard/overview',
//         DASHBOARD_MEMBERS: '/api/dashboard/members',
//         DASHBOARD_REVENUE: '/api/dashboard/revenue',
//         DASHBOARD_CLASSES: '/api/dashboard/classes',

//         // Notifications
//         NOTIFICATIONS_CLASS_REMINDERS: (classId) => `/api/notifications/class-reminders/${classId}`,
//         NOTIFICATIONS_PAYMENT_REMINDERS: '/api/notifications/payment-reminders',
//         NOTIFICATIONS_BULK: '/api/notifications/bulk',

//         // Admin
//         ADMINS: '/api/admins',
//         ADMIN_BY_ID: (id) => `/api/admins/${id}`,
//         ADMIN_STATISTICS: '/api/admins/statistics',

//         // Progress
//         PROGRESS: '/api/progress',
//         PROGRESS_BY_ID: (id) => `/api/progress/${id}`,

//         // Workouts
//         WORKOUTS: '/api/workouts',
//         WORKOUT_BY_ID: (id) => `/api/workouts/${id}`
//     }
// };

// // HTTP Headers
// const getHeaders = (includeAuth = true) => {
//     const headers = {
//         'Content-Type': 'application/json',
//         'Accept': 'application/json'
//     };

//     if (includeAuth) {
//         const token = localStorage.getItem('authToken');
//         if (token) {
//             headers['Authorization'] = `Bearer ${token}`;
//         }
//     }

//     return headers;
// };

// // API Helper Functions
// const apiCall = async(endpoint, options = {}) => {
//     const url = `${API_CONFIG.BASE_URL}${endpoint}`;
//     const config = {
//         headers: getHeaders(options.includeAuth !== false),
//         ...options
//     };

//     try {
//         const response = await fetch(url, config);

//         if (!response.ok) {
//             const errorData = await response.json().catch(() => ({}));
//             throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
//         }

//         return await response.json();
//     } catch (error) {
//         console.error('API call failed:', error);
//         throw error;
//     }
// };

// // Export for use in other files
// if (typeof module !== 'undefined' && module.exports) {
//     module.exports = { API_CONFIG, getHeaders, apiCall };
// } else {
//     window.API_CONFIG = API_CONFIG;
//     window.getHeaders = getHeaders;
//     window.apiCall = apiCall;
// }