// Authentication state
let isAuthenticated = false;
let currentUser = null;

// Ensure apiService is available globally from where it's defined
// (e.g., if you put the ApiService class in its own file and load it first,
// or if you define it globally in script.js)
// No need to instantiate here if it's already a global instance from the provided ApiService code.
// const apiService = new ApiService(); // REMOVE or comment out this line if ApiService is globally instantiated elsewhere

// Password validation rules
const passwordRules = {
    minLength: 8,
    hasUpperCase: /[A-Z]/,
    hasNumber: /[0-9]/,
    hasSpecialChar: /[!@#$%^&*(),.?":{}|<>]/
};

// Check authentication status on page load
document.addEventListener('DOMContentLoaded', () => {
    // Ensure apiService is available before calling its methods
    if (window.apiService) {
        checkAuth();
        setupLoginForm();
        setupRegistrationForm();
        setupPasswordReset();
    } else {
        console.error("ApiService not found. Make sure apiService.js (or equivalent) is loaded before auth.js.");
    }
});

// Check if user is authenticated
async function checkAuth() {
    const token = localStorage.getItem('authToken');

    if (!token) {
        // If on protected pages and not authenticated, redirect to memberships
        if (isProtectedPage() && !isAuthenticated) {
            window.location.href = 'memberships.html';
        }
    } else {
        try {
            // Verify token with backend using the global apiService instance
            const response = await window.apiService.verifyToken(); // Assuming refreshToken exists in ApiService
            isAuthenticated = true;
            currentUser = response.user;
            localStorage.setItem('currentUser', JSON.stringify(response.user)); // Update currentUser in localStorage

            // Check if user has access to the current page based on their role
            if (isProtectedPage()) {
                const currentPage = window.location.pathname.split('/').pop();
                if (!hasAccessToPage(currentPage, currentUser.role)) {
                    window.location.href = 'memberships.html';
                }
            }
        } catch (error) {
            console.error('Token verification failed:', error);
            logout();
        }
    }
}

// Validate password strength
function validatePassword(password) {
    const errors = [];

    if (password.length < passwordRules.minLength) {
        errors.push(`Password must be at least ${passwordRules.minLength} characters long`);
    }
    if (!passwordRules.hasUpperCase.test(password)) {
        errors.push('Password must contain at least one uppercase letter');
    }
    if (!passwordRules.hasNumber.test(password)) {
        errors.push('Password must contain at least one number');
    }
    if (!passwordRules.hasSpecialChar.test(password)) {
        errors.push('Password must contain at least one special character');
    }

    return errors;
}

// Setup login form handler
function setupLoginForm() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async(e) => {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const errorMessage = document.getElementById('errorMessage');

            // Clear previous error messages
            if (errorMessage) {
                errorMessage.textContent = '';
                errorMessage.style.display = 'none';
            }

            try {
                // Use the global apiService instance for login
                const response = await window.apiService.login(username, password);

                // Store token and user info
                localStorage.setItem('authToken', response.token);
                localStorage.setItem('userRole', response.user.role);
                localStorage.setItem('userId', response.user.id);
                localStorage.setItem('currentUser', JSON.stringify(response.user));
                isAuthenticated = true;
                currentUser = response.user;

                // Immediately update the profile details on the page (if a user is present)
                if (typeof updateProfileDetailsOnPage === 'function') {
                    updateProfileDetailsOnPage(response.user);
                }

                // Redirect based on role
                redirectBasedOnRole(response.user.role);
            } catch (error) {
                console.error('Login error:', error);
                if (errorMessage) {
                    errorMessage.textContent = error.message || 'Invalid username or password';
                    errorMessage.style.display = 'block';
                }
            }
        });
    }
}

// Setup registration form handler
function setupRegistrationForm() {
    const registrationForm = document.getElementById('registrationForm');
     const regErrorMessage = document.getElementById('regErrorMessage'); // This is your error message display

    if (registrationForm) {
        

        // Pre-select plan if coming from memberships page
        const urlParams = new URLSearchParams(window.location.search);
        const selectedPlan = urlParams.get('plan');
        if (selectedPlan) {
            const planSelect = document.getElementById('planSelect');
            if (planSelect) {
                planSelect.value = selectedPlan;
            }
        }

        registrationForm.addEventListener('submit', async(e) => {
            e.preventDefault();
            const email = document.getElementById('regEmail').value;
            const password = document.getElementById('regPassword').value;
            const confirmPassword = document.getElementById('regConfirmPassword').value;
            const plan = document.getElementById('planSelect').value;
            const firstName = document.getElementById('firstName').value;
            const lastName = document.getElementById('lastName').value;
            const errorMessage = document.getElementById('regErrorMessage');

            // Clear previous error messages
            if (errorMessage) {
                errorMessage.textContent = '';
                errorMessage.style.display = 'none';
            }

            // Check if passwords match
            if (password !== confirmPassword) {
                if (errorMessage) {
                    errorMessage.textContent = 'Passwords do not match';
                    errorMessage.style.display = 'block';
                }
                return;
            }

            // Validate password strength
            const passwordErrors = validatePassword(password);
            if (passwordErrors.length > 0) {
                if (errorMessage) {
                    errorMessage.textContent = passwordErrors.join('\n');
                    errorMessage.style.display = 'block';
                }
                return;
            }

            try {
                // Register user with backend using the global apiService instance
                const response = await window.apiService.register({
                    email,
                    password,
                    username: email, // Assuming username is the same as email
                    firstName,
                    lastName,
                    role: 'ROLE_MEMBER', // Default role for registration
                    plan: plan // Include plan in registration data
                });
                
                // Store token and user info (already handled by ApiService.register, but good to ensure consistency)
                localStorage.setItem('authToken', response.token);
                localStorage.setItem('userRole', response.user.role);
                localStorage.setItem('userId', response.user.id);
                localStorage.setItem('currentUser', JSON.stringify(response.user));
                isAuthenticated = true;
                currentUser = response.user;

                // Redirect based on role
                redirectBasedOnRole(response.user.role);
            } catch (error) {
                console.error('Registration error:', error);
                if (errorMessage) {
                    errorMessage.textContent = error.message || 'An error occurred. Please try again.';
                    errorMessage.style.display = 'block';
                }
            }
        });
    }
}

// Setup password reset handler
function setupPasswordReset() {
    const resetForm = document.getElementById('resetPasswordForm');
    if (resetForm) {
        resetForm.addEventListener('submit', async(e) => {
            e.preventDefault();
            const email = document.getElementById('resetEmail').value;
            const resetMessage = document.getElementById('resetMessage');

            // Clear previous messages
            if (resetMessage) {
                resetMessage.textContent = '';
                resetMessage.style.color = '';
            }

            try {
                // Use the global apiService instance for password reset
                const response = await window.apiService.apiCall('/auth/reset-password', {
                    method: 'POST',
                    body: JSON.stringify({ email }),
                    skipAuth: true
                });

                if (resetMessage) {
                    resetMessage.textContent = 'Password reset instructions sent to your email';
                    resetMessage.style.color = 'green';
                }
            } catch (error) {
                console.error('Password reset error:', error);
                if (resetMessage) {
                    resetMessage.textContent = error.message || 'Password reset failed';
                    resetMessage.style.color = 'red';
                }
            }
        });
    }
}

// Check if current page is protected
function isProtectedPage() {
    const protectedPages = [ 'tracker.html', 'dashboard.html']; // Added profile and dashboard
    const currentPage = window.location.pathname.split('/').pop();
    return protectedPages.includes(currentPage);
}

// Check if user's role has access to the page
function hasAccessToPage(page, role) {
    const accessRules = {
       // 'classes.html': ['MEMBER', 'TRAINER', 'ADMIN'],
        'tracker.html': ['MEMBER', 'TRAINER', 'ADMIN'],
      //  'trainer.html': ['TRAINER', 'ADMIN'], // Typically only trainers/admins access trainer page
       // 'profile.html': ['MEMBER', 'TRAINER', 'ADMIN'],
        'dashboard.html': ['ADMIN'] // Only admins access dashboard
    };

    // If a page is not in protectedPages, it's considered public access by default.
    // If it is in protectedPages, check if the role has access.
    return !isProtectedPage(page) || (accessRules[page] && accessRules[page].includes(role));
}

// Logout function
async function logout() {
    try {
        // Call backend logout endpoint using the global apiService instance
        await window.apiService.logout();
    } catch (error) {
        console.error('Logout error:', error);
    } finally {
        // Clear local storage
        localStorage.removeItem('authToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userId');
        localStorage.removeItem('currentUser');
        isAuthenticated = false;
        currentUser = null;
        window.location.href = 'memberships.html'; // Redirect to memberships after logout
    }
}

// Function to handle redirection based on user's role
function redirectBasedOnRole(role) {
    switch (role) {
        case 'ROLE_MEMBER': // Assuming backend sends 'ROLE_MEMBER'
            window.location.href = 'classes.html';
            break;
        case 'ROLE_TRAINER': // Assuming backend sends 'ROLE_TRAINER'
            window.location.href = 'trainer.html';
            break;
        case 'ROLE_ADMIN': // Assuming backend sends 'ROLE_ADMIN'
            window.location.href = 'dashboard.html';
            break;
        default:
            // Fallback for unknown roles or if role is missing
            console.warn('Unknown user role or no role provided, redirecting to classes.html');
            window.location.href = 'classes.html';
    }
}

// Get current user info
function getCurrentUser() {
    // Use the ApiService's getCurrentUser method if available and properly initialized
    if (window.apiService && typeof window.apiService.getCurrentUser === 'function') {
        const user = window.apiService.getCurrentUser();
        // The ApiService's getCurrentUser might return basic info,
        // if currentUser from checkAuth has more details, prefer that.
        if (currentUser && currentUser.id === user.id) {
            return currentUser;
        }
        // Otherwise, parse from localStorage for more details if available
        const userStr = localStorage.getItem('currentUser');
        if (userStr) {
            try {
                return JSON.parse(userStr);
            } catch {
                return null;
            }
        }
    }
    return null; // Fallback if apiService or localStorage parsing fails
}

// Check if user is authenticated
function isUserAuthenticated() {
    // Use the ApiService's isAuthenticated method
    return window.apiService ? window.apiService.isAuthenticated() : false;
}

// Export functions for use in other files
// This makes these functions accessible via window.auth
// auth.js
// This file should define how user authentication state is managed

    (function () {
    if (typeof window.apiService === 'undefined') {
        console.error('Error: ApiService is not available. Check script loading order.');
        return;
    }
})();
    // Define the global 'auth' object that other scripts (like script.js) depend on.
    window.auth = {
        // Retrieves the full user object from localStorage
        getCurrentUser: function() {
            const userJson = localStorage.getItem('currentUser');
            if (userJson) {
                try {
                    return JSON.parse(userJson);
                } catch (e) {
                    console.error("Error parsing user from localStorage:", e);
                    return null;
                }
            }
            return null; // No user found
        },

        // Logs the user in using the API and stores the full user object.
        login: async function(username, password) {
            try {
                // Call your API login method
                const response = await window.apiService.login(username, password);

                // Store the full user object in localStorage
                localStorage.setItem('currentUser', JSON.stringify(response.user));
                
                // Store the auth token for subsequent API calls
                localStorage.setItem('authToken', response.token);

                // Re-initialize the token in apiService instance
                window.apiService.refreshToken();

                return response.user;
            } catch (error) {
                console.error("Login failed:", error);
                // Clear any partial user data on failed login
                localStorage.removeItem('currentUser');
                localStorage.removeItem('authToken');
                throw error;
            }
        },

        // Logs the user out and clears all user data from localStorage.
        logout: async function() {
            try {
                await window.apiService.logout();
            } catch (error) {
                console.warn("Logout failed, but proceeding with client-side cleanup:", error);
            } finally {
                localStorage.removeItem('currentUser');
                localStorage.removeItem('authToken');
                // We should also clear any other user-specific data here
                window.location.href = 'index.html'; // Redirect to the home page
            }
        }
    };

