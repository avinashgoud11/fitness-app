const hamburger = document.querySelector('.hamburger');
const navLinks = document.querySelector('.nav-links');

hamburger.addEventListener('click', () => {
    navLinks.classList.toggle('active');
    hamburger.classList.toggle("active");
});

function handleMenuKeyPress(event) {
    if (event.key === 'Enter' || event.key === ' ') {
        toggleMenu();
    }
}

function toggleMenu() {
    document.querySelector('.nav-links').classList.toggle('active');
}

// Load users from backend API
async function loadUsers() {
    try {
        // Use the global apiService instance
        const users = await window.apiService.getMembers(); // Assuming getMembers exists in ApiService
        const userList = document.getElementById('user-list');
        if (userList) {
            userList.innerHTML = '';
            users.forEach(user => {
                const li = document.createElement('li');
                li.textContent = `${user.user.firstName} ${user.user.lastName}`;
                userList.appendChild(li);
            });
        }
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}

// Login function using backend API (This function is now redundant as auth.js handles login)
// Keeping it commented out for reference, but it should not be called directly.
/*
async function login(email, password) {
    try {
        const response = await window.apiService.login(email, password);
        console.log('Login successful:', response);
        return response;
    } catch (error) {
        console.error('Login failed:', error);
        throw error;
    }
}
*/

// Create workout using backend API
async function createWorkout(workoutData) {
    try {
        // Use the global apiService instance
        const response = await window.apiService.createProgress(workoutData); // Assuming createProgress for workouts
        console.log('Workout created:', response);
        return response;
    } catch (error) {
        console.error('Failed to create workout:', error);
        throw error;
    }
}

// Load classes from backend API
async function loadClasses() {
    try {
        // Use the global apiService instance
        const classes = await window.apiService.getClasses();
        return classes;
    } catch (error) {
        console.error('Error fetching classes:', error);
        return [];
    }
}

// Book a class
async function bookClass(classId, memberId) {
    try {
        // Use the global apiService instance
        const response = await window.apiService.createBooking({
            fitnessClassId: classId,
            memberId: memberId,
            status: 'PENDING'
        });

        console.log('Class booked:', response);
        return response;
    } catch (error) {
        console.error('Failed to book class:', error);
        throw error;
        
    }
}

function renderUserProfile() {
    const user = window.auth && typeof window.auth.getCurrentUser === 'function'
        ? window.auth.getCurrentUser()
        : null;

    const userProfileDiv = document.getElementById('user-profile');
    const greetingSpan = document.getElementById('greeting');

    if (!userProfileDiv) return;

    if (user && user.id) {
        // Use profile image if available, else fallback to initials
        const profileImage = user.profileImage || null;
        const initials = (user.firstName?.[0] || '') + (user.lastName?.[0] || '');

        userProfileDiv.innerHTML = `
            <a href="profile.html" title="Go to Profile">
                ${
                    profileImage
                        ? `<img src="${profileImage}" alt="Profile" style="width:40px; height:40px; border-radius:50%; object-fit:cover; border:2px solid #3bd17e;">`
                        : `<div style="width:40px; height:40px; background-color:#3bd17e; color:#fff; display:flex; align-items:center; justify-content:center; border-radius:50%; font-weight:bold; font-size:16px;">
                            ${initials || 'U'}
                            </div>`
                }
            </a>
        `;

        if (greetingSpan) {
            greetingSpan.textContent = `Welcome, ${user.firstName || user.name || 'User'}!`;
        }
    } else {
        userProfileDiv.innerHTML = `
            <button class="login-button" onclick="showLoginModal()">Login</button>
        `;
        if (greetingSpan) greetingSpan.textContent = '';
    }
}



// Function to show a custom confirmation modal (replaces alert/confirm)
function showConfirmationModal(message, onConfirm) {
    let modal = document.getElementById('customConfirmationModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'customConfirmationModal';
        modal.className = 'modal'; // Use your existing modal styling
        modal.innerHTML = `
            <div class="modal-content">
                <span class="close-modal">&times;</span>
                <p id="confirmationMessage"></p>
                <div class="modal-actions">
                    <button id="confirmYes" class="cta-button">Yes</button>
                    <button id="confirmNo" class="secondary-button">No</button>
                </div>
            </div>
        `;
        document.body.appendChild(modal);

        modal.querySelector('.close-modal').addEventListener('click', () => modal.style.display = 'none');
        modal.querySelector('#confirmNo').addEventListener('click', () => modal.style.display = 'none');
        modal.querySelector('#confirmYes').addEventListener('click', () => {
            onConfirm();
            modal.style.display = 'none';
        });
    }
    modal.querySelector('#confirmationMessage').textContent = message;
    modal.style.display = 'block';
}


// Initialize page functionality
document.addEventListener('DOMContentLoaded', () => {
    // Load users if on a page that needs them
    if (document.getElementById('user-list')) {
        loadUsers();
    }

    // Load classes if on classes page
    if (window.location.pathname.includes('classes.html')) {
        loadClasses().then(classes => {
            // Update the classes display with real data
            updateClassesDisplay(classes);
        });
    }
    renderUserProfile(); // Render user profile on DOMContentLoaded
});

// Update classes display with real data
function updateClassesDisplay(classes) {
    const scheduleGrid = document.querySelector('.schedule-grid');
    if (!scheduleGrid || !classes.length) return;

    // Group classes by day
    const classesByDay = {};
    classes.forEach(cls => {
        // Ensure cls.startTime is a valid date string
        const date = new Date(cls.startTime);
        if (isNaN(date.getTime())) {
            console.warn('Invalid startTime for class:', cls);
            return; // Skip this class if startTime is invalid
        }
        const day = date.toLocaleDateString('en-US', { weekday: 'long' });
        if (!classesByDay[day]) {
            classesByDay[day] = [];
        }
        classesByDay[day].push(cls);
    });

    // Clear existing class entries before adding new ones
    scheduleGrid.querySelectorAll('.day-card ul').forEach(ul => {
        ul.innerHTML = '';
    });

    // Update the schedule display
    Object.keys(classesByDay).forEach(day => {
        const dayCard = scheduleGrid.querySelector(`[data-day="${day}"]`);
        if (dayCard) {
            const classList = dayCard.querySelector('ul');
            // Sort classes by time for consistent display
            classesByDay[day].sort((a, b) => new Date(a.startTime).getTime() - new Date(b.startTime).getTime());

            classesByDay[day].forEach(cls => {
                const li = document.createElement('li');
                li.innerHTML = `
                    <div class="class-info">
                        <span class="time">${new Date(cls.startTime).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })}</span>
                        <span class="class-name">${cls.name}</span>
                        <span class="instructor">${cls.trainer.user.firstName} ${cls.trainer.user.lastName}</span>
                    </div>
                    <button class="book-btn" data-class-id="${cls.id}">Book Now</button>
                `;
                classList.appendChild(li);
            });
        }
    });

    // Add event listeners for "Book Now" buttons
    document.querySelectorAll('.book-btn').forEach(button => {
        button.addEventListener('click', async (event) => {
            const classId = event.target.dataset.classId;
            const memberId = window.auth.getUserId(); // Get current user's ID

            if (!memberId) {
                showInfoModal('Please log in to book a class.', () => {
                    // Optional: redirect to login/memberships page
                    window.location.href = 'memberships.html';
                });
                return;
            }

            try {
                await bookClass(classId, memberId);
                showInfoModal('Class booked successfully!');
            } catch (error) {
                console.error('Booking failed:', error);
                showInfoModal(error.message || 'Failed to book class. Please try again.');
            }
        });
    });
}

// Function to show a custom info modal (replaces alert)
function showInfoModal(message, onConfirm = () => {}) {
    let modal = document.getElementById('customInfoModal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'customInfoModal';
        modal.className = 'modal'; // Use your existing modal styling
        modal.innerHTML = `
            <div class="modal-content">
                <span class="close-modal">&times;</span>
                <p id="infoMessage"></p>
                <div class="modal-actions">
                    <button id="infoOk" class="cta-button">OK</button>
                </div>
            </div>
        `;
        document.body.appendChild(modal);

        modal.querySelector('.close-modal').addEventListener('click', () => {
            modal.style.display = 'none';
            onConfirm();
        });
        modal.querySelector('#infoOk').addEventListener('click', () => {
            modal.style.display = 'none';
            onConfirm();
        });
    }
    modal.querySelector('#infoMessage').textContent = message;
    modal.style.display = 'block';
}

// Function to show the login modal (for the "Login" button in navbar)
function showLoginModal() {
    const loginModal = document.getElementById('loginModal');
    if (loginModal) {
        loginModal.style.display = 'block';
    }
}
const API_CONFIG = {
    BASE_URL: "https://fitness-app-0zk0.onrender.com", // Update with your API base
    ENDPOINTS: {
        LOGIN: "/auth/login",
        MEMBERS: "/members",
        CLASSES: "/classes",
        BOOKINGS: "/class-bookings",
        WORKOUTS: "/workouts"
    }
};

async function apiCall(endpoint, options = {}) {
    const url = API_CONFIG.BASE_URL + endpoint;
    const headers = {
        'Content-Type': 'application/json'
    };

    const token = localStorage.getItem('authToken');
    if (token && options.includeAuth !== false) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(url, {
        method: options.method || 'GET',
        headers: headers,
        body: options.body || null
    });

    if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'API error');
    }

    return await response.json();
}

