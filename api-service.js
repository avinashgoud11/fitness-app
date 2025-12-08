// API Service for Frontend-Backend Communication
class ApiService {
    constructor() {
        this.baseURL = 'https://fitness-app-0zk0.onrender.com/api';
        this.token = localStorage.getItem('authToken');
    }

    // Generic API call method
    async apiCall(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const config = {
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                ...options.headers
            },
            ...options
        };

        // Add authorization header if token exists
        if (this.token && !options.skipAuth) {
            config.headers['Authorization'] = `Bearer ${this.token}`;
        }

        try {
            const response = await fetch(url, config);

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            } else {
                return await response.text();
            }
        } catch (error) {
            console.error('API call failed:', error);
            throw error;
        }
    }

    // Authentication Methods
    async login(username, password) {
        const response = await this.apiCall('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password }),
            skipAuth: true
        });

        this.token = response.token;
        localStorage.setItem('authToken', response.token);
        // localStorage.setItem('userRole', response.user.role);
        // localStorage.setItem('userId', response.user.id);

        return response;
    }
    // Rename refreshToken to a more descriptive name
// updateTokenFromStorage() {
// this.token = localStorage.getItem('authToken');
// }

    async register(userData) {
        const response = await this.apiCall('/auth/register', {
            method: 'POST',
            body: JSON.stringify(userData),
            skipAuth: true
        });

        this.token = response.token;
        localStorage.setItem('authToken', response.token);
        localStorage.setItem('userRole', response.user.role);
        localStorage.setItem('userId', response.user.id);

        return response;
    }

    async logout() {
    try {
        const token = localStorage.getItem('authToken'); // Get the token

        // Check if the token exists before sending the request
        if (token) {
            await this.apiCall('/auth/logout', {
                method: 'POST',
                                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` // Still a good practice to send it here too
                },
                // Send the token in the request body, which the backend is expecting
                body: JSON.stringify({ token: token })
            });
        }

    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        this.token = null;
        localStorage.removeItem('authToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userId');
    }
}
    async verifyToken() {
        // We'll call a simple protected endpoint that just returns the current user
        // The backend will verify the token from the 'Authorization' header
        return await this.apiCall('/auth/me');
    }
    // Member Management
    async createMember(memberData) {
        return await this.apiCall('/members', {
            method: 'POST',
            body: JSON.stringify(memberData)
        });
    }

    async getMembers() {
        return await this.apiCall('/members');
    }

    async getMemberById(id) {
        return await this.apiCall(`/members/${id}`);
    }

    async updateMember(id, memberData) {
        return await this.apiCall(`/members/${id}`, {
            method: 'PUT',
            body: JSON.stringify(memberData)
        });
    }

    async deleteMember(id) {
        return await this.apiCall(`/members/${id}`, {
            method: 'DELETE'
        });
    }

    // Class Management
    async getClasses() {
        return await this.apiCall('/classes');
    }

    async createClass(classData) {
        return await this.apiCall('/classes', {
            method: 'POST',
            body: JSON.stringify(classData)
        });
    }

    async getClassById(id) {
        return await this.apiCall(`/classes/${id}`);
    }

    async updateClass(id, classData) {
        return await this.apiCall(`/classes/${id}`, {
            method: 'PUT',
            body: JSON.stringify(classData)
        });
    }

    async deleteClass(id) {
        return await this.apiCall(`/classes/${id}`, {
            method: 'DELETE'
        });
    }

    // Booking Management
    async createBooking(bookingData) {
        return await this.apiCall('/class-bookings', {
            method: 'POST',
            body: JSON.stringify(bookingData)
        });
    }

    async getBookings() {
        return await this.apiCall('/bookings');
    }

    async getBookingById(id) {
        return await this.apiCall(`/bookings/${id}`);
    }

    async updateBooking(id, bookingData) {
        return await this.apiCall(`/bookings/${id}`, {
            method: 'PUT',
            body: JSON.stringify(bookingData)
        });
    }

    async cancelBooking(id) {
        return await this.apiCall(`/bookings/${id}`, {
            method: 'DELETE'
        });
    }

    // Payment Management
    async createPayment(paymentData) {
        return await this.apiCall('/payments', {
            method: 'POST',
            body: JSON.stringify(paymentData)
        });
    }

    async getPayments() {
        return await this.apiCall('/payments');
    }

    async getPaymentById(id) {
        return await this.apiCall(`/payments/${id}`);
    }

    async updatePaymentStatus(id, status) {
        return await this.apiCall(`/payments/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        });
    }

    // Trainer Management
    async createTrainer(trainerData) {
        return await this.apiCall('/trainers', {
            method: 'POST',
            body: JSON.stringify(trainerData)
        });
    }

    async getTrainers() {
        return await this.apiCall('/trainers');
    }

    async getTrainerById(id) {
        return await this.apiCall(`/trainers/${id}`);
    }

    async updateTrainer(id, trainerData) {
        return await this.apiCall(`/trainers/${id}`, {
            method: 'PUT',
            body: JSON.stringify(trainerData)
        });
    }

    // Progress Tracking
    async createProgress(progressData) {
        return await this.apiCall('/progress', {
            method: 'POST',
            body: JSON.stringify(progressData)
        });
    }

    async getProgress() {
        return await this.apiCall('/progress');
    }

    async getProgressById(id) {
        return await this.apiCall(`/progress/${id}`);
    }

    async updateProgress(id, progressData) {
        return await this.apiCall(`/progress/${id}`, {
            method: 'PUT',
            body: JSON.stringify(progressData)
        });
    }

    // Contact Messages
    async sendContactMessage(messageData, options) {
        return await this.apiCall('/contact-messages', {
            method: 'POST',
            body: JSON.stringify(messageData),
            skipAuth: true
        });
    }

    async getContactMessages() {
        return await this.apiCall('/contact-messages');
    }

    // Dashboard Analytics
    async getDashboardOverview() {
        return await this.apiCall('/dashboard/overview');
    }

    async getMemberAnalytics() {
        return await this.apiCall('/dashboard/members');
    }

    async getRevenueAnalytics() {
        return await this.apiCall('/dashboard/revenue');
    }

    async getClassAnalytics() {
        return await this.apiCall('/dashboard/classes');
    }

    // Notifications
    async sendClassReminders(classId) {
        return await this.apiCall(`/notifications/class-reminders/${classId}`, {
            method: 'POST'
        });
    }

    async sendPaymentReminders() {
        return await this.apiCall('/notifications/payment-reminders', {
            method: 'POST'
        });
    }

    async sendBulkNotification(subject, message) {
        return await this.apiCall('/notifications/bulk', {
            method: 'POST',
            body: JSON.stringify({ subject, message })
        });
    }

    // Admin Management
    async createAdmin(adminData) {
        return await this.apiCall('/admins', {
            method: 'POST',
            body: JSON.stringify(adminData)
        });
    }

    async getAdmins() {
        return await this.apiCall('/admins');
    }

    async getAdminStatistics() {
        return await this.apiCall('/admins/statistics');
    }

    // Utility Methods
    isAuthenticated() {
        return !!this.token;
    }

    getCurrentUser() {
        return {
            id: localStorage.getItem('userId'),
            role: localStorage.getItem('userRole')
        };
    }

    refreshToken() {
        this.token = localStorage.getItem('authToken');
    }
}

// Create global instance
const apiService = new ApiService();

// Export for use in other files
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ApiService;
} else {
    window.apiService = apiService;
}