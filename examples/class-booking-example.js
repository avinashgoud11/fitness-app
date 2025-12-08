// Example: How to Book a Class
// This shows the complete flow from frontend to backend to database

class ClassBookingExample {
    constructor() {
        this.apiService = window.apiService;
        this.setupEventListeners();
    }

    setupEventListeners() {
        // Listen for booking button clicks
        document.addEventListener('click', (e) => {
            if (e.target.classList.contains('book-btn')) {
                this.handleBookingClick(e.target);
            }
        });

        // Load classes when page loads
        document.addEventListener('DOMContentLoaded', () => {
            this.loadClasses();
        });
    }

    async handleBookingClick(button) {
        try {
            // Show loading state
            button.disabled = true;
            button.textContent = 'Booking...';

            // Get class data from button attributes
            const classId = button.dataset.classId;
            const className = button.dataset.className;
            const classTime = button.dataset.classTime;

            // Get current user
            const currentUser = this.apiService.getCurrentUser();
            if (!currentUser.id) {
                throw new Error('User not authenticated');
            }

            // Create booking data
            const bookingData = {
                fitnessClassId: parseInt(classId),
                memberId: parseInt(currentUser.id),
                status: 'PENDING',
                bookingDate: new Date().toISOString()
            };

            console.log('Sending booking request:', bookingData);

            // Send request to backend
            const response = await this.apiService.createBooking(bookingData);

            console.log('Booking successful:', response);

            // Update UI
            button.textContent = 'Booked!';
            button.classList.add('booked');
            button.disabled = true;

            // Show success message
            this.showMessage('Class booked successfully!', 'success');

            // Optionally create payment record
            await this.createPaymentRecord(response.id, className);

        } catch (error) {
            console.error('Booking failed:', error);

            // Reset button
            button.disabled = false;
            button.textContent = 'Book Now';

            // Show error message
            this.showMessage(`Booking failed: ${error.message}`, 'error');
        }
    }

    async loadClasses() {
        try {
            console.log('Loading classes from backend...');

            // Get classes from backend
            const classes = await this.apiService.getClasses();

            console.log('Classes loaded:', classes);

            // Update the UI with real data
            this.updateClassesDisplay(classes);

        } catch (error) {
            console.error('Failed to load classes:', error);
            this.showMessage('Failed to load classes', 'error');
        }
    }

    updateClassesDisplay(classes) {
            const scheduleGrid = document.querySelector('.schedule-grid');
            if (!scheduleGrid) return;

            // Clear existing content
            scheduleGrid.innerHTML = '';

            // Group classes by day
            const classesByDay = {};
            classes.forEach(cls => {
                const day = new Date(cls.startTime).toLocaleDateString('en-US', { weekday: 'long' });
                if (!classesByDay[day]) {
                    classesByDay[day] = [];
                }
                classesByDay[day].push(cls);
            });

            // Create day cards
            Object.keys(classesByDay).forEach(day => {
                        const dayCard = document.createElement('div');
                        dayCard.className = 'schedule-card';
                        dayCard.dataset.day = day;

                        dayCard.innerHTML = `
                <h3>${day}</h3>
                <ul>
                    ${classesByDay[day].map(cls => `
                        <li>
                            <div class="class-info">
                                <span class="time">${new Date(cls.startTime).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' })}</span>
                                <span class="class-name">${cls.name}</span>
                                <span class="instructor">${cls.trainer.user.firstName} ${cls.trainer.user.lastName}</span>
                                <span class="capacity">${cls.currentEnrollment}/${cls.maxCapacity}</span>
                            </div>
                            <button class="book-btn" 
                                data-class-id="${cls.id}"
                                data-class-name="${cls.name}"
                                data-class-time="${cls.startTime}"
                                ${cls.currentEnrollment >= cls.maxCapacity ? 'disabled' : ''}>
                                ${cls.currentEnrollment >= cls.maxCapacity ? 'Full' : 'Book Now'}
                            </button>
                        </li>
                    `).join('')}
                </ul>
            `;

            scheduleGrid.appendChild(dayCard);
        });
    }

    async createPaymentRecord(bookingId, className) {
        try {
            const currentUser = this.apiService.getCurrentUser();
            
            const paymentData = {
                memberId: parseInt(currentUser.id),
                fitnessClassId: bookingId,
                amount: 25.00, // Default class price
                paymentMethod: 'CASH',
                status: 'PENDING',
                paymentDate: new Date().toISOString(),
                description: `Payment for ${className}`
            };

            console.log('Creating payment record:', paymentData);

            const payment = await this.apiService.createPayment(paymentData);

            console.log('Payment record created:', payment);

        } catch (error) {
            console.error('Failed to create payment record:', error);
        }
    }

    showMessage(message, type = 'info') {
        // Create message element
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${type}`;
        messageDiv.textContent = message;

        // Add to page
        document.body.appendChild(messageDiv);

        // Remove after 3 seconds
        setTimeout(() => {
            messageDiv.remove();
        }, 3000);
    }
}

// Initialize the example
const classBookingExample = new ClassBookingExample();