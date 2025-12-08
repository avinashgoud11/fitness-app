// Security token generation
        function generateCSRFToken() {
            return Math.random().toString(36).substring(2) + Date.now().toString(36);
        }

        // Input validation
        function validateInput(input) {
            return input.replace(/[<>]/g, '');
        }

        // Rate limiting
        const rateLimiter = {
            attempts: 0,
            lastAttempt: 0,
            maxAttempts: 5,
            timeWindow: 60000, // 1 minute

            checkLimit() {
                const now = Date.now();
                if (now - this.lastAttempt > this.timeWindow) {
                    this.attempts = 0;
                    this.lastAttempt = now;
                }
                this.attempts++;
                this.lastAttempt = now;
                return this.attempts <= this.maxAttempts;
            }
        };

        // Hamburger Menu
        const hamburger = document.querySelector(".hamburger");
        const navLinks = document.querySelector(".nav-links");

        hamburger.addEventListener("click", () => {
            navLinks.classList.toggle("active");
            hamburger.classList.toggle("active");
        });

        // Class Filtering
        const filterButtons = document.querySelectorAll('.filter-btn');
        const scheduleCards = document.querySelectorAll('.schedule-card');

        filterButtons.forEach(button => {
            button.addEventListener('click', () => {
                filterButtons.forEach(btn => btn.classList.remove('active'));
                button.classList.add('active');

                const filter = button.getAttribute('data-filter');

                scheduleCards.forEach(card => {
                    if (filter === 'all' || card.getAttribute('data-category') === filter) {
                        card.style.display = 'block';
                    } else {
                        card.style.display = 'none';
                    }
                });
            });
        });

        // Booking Modal with Security
        const modal = document.getElementById('bookingModal');
        const bookButtons = document.querySelectorAll('.book-btn');
        const closeModal = document.querySelector('.close-modal');
        const bookingForm = document.getElementById('bookingForm');
        

        // Add CSRF token to form
        const csrfToken = generateCSRFToken();
        const csrfInput = document.createElement('input');
        csrfInput.type = 'hidden';
        csrfInput.name = '_csrf';
        csrfInput.value = csrfToken;
        bookingForm.appendChild(csrfInput);

        bookButtons.forEach(button => {
            button.addEventListener('click', () => {
                if (!rateLimiter.checkLimit()) {
                    alert('Too many booking attempts. Please try again later.');
                    return;
                }

                const className = validateInput(button.getAttribute('data-class'));
                const classTime = validateInput(button.getAttribute('data-time'));
                const instructor = validateInput(button.getAttribute('data-instructor'));

                document.getElementById('className').value = className;
                document.getElementById('classTime').value = classTime;
                document.getElementById('instructor').value = instructor;

                modal.style.display = 'block';
            });
        });

        closeModal.addEventListener('click', () => {
            modal.style.display = 'none';
        });

        window.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.style.display = 'none';
            }
        });
        // Secure form submission
bookingForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Rate limiter check
    if (!rateLimiter.checkLimit()) {
        alert('Too many booking attempts. Please try again later.');
        return;
    }

    const formData = new FormData(bookingForm);
    const data = Object.fromEntries(formData.entries());

    // Input validation
    for (let key in data) {
        data[key] = validateInput(data[key]);
    }

    // Add CSRF token if your backend requires it
    data.csrfToken = csrfToken;

    try {
        const response = await fetch('https://fitness-app-0zk0.onrender.com/api/class-bookings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-Token': csrfToken // optional, only if Spring Security uses it
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error('Booking failed');
        }

        await response.json();
        alert('✅ Booking confirmed! You will receive a confirmation email shortly.');
        modal.style.display = 'none';
        bookingForm.reset();
    } catch (error) {
        console.error('Booking error:', error);
        alert('❌ There was an error processing your booking. Please try again later.');
    }
});

            // const cancelBookingBtn = document.getElementById('cancelBookingBtn');
            // if (cancelBookingBtn) {
            //     cancelBookingBtn.addEventListener('click', () => {
            //         modal.style.display = 'none';
            //         bookingForm.reset();
            //     });
            // }
