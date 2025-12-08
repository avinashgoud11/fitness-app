document.addEventListener('DOMContentLoaded', () => {
    const contactForm = document.getElementById('contactForm');
    const successModal = document.getElementById('successModal');
    const closeModal = successModal ? successModal.querySelector('.close-modal') : null;

    // Validation patterns
    const patterns = {
        name: /^[a-zA-Z\s]{2,50}$/,
        email: /^[^@\s]+@[^@\s]+\.[^@\s]+$/, // Corrected regex for email to be more standard
        phone: /^[\d\s\-+()]{10,15}$/
    };

    // Error messages
    const errorMessages = {
        name: 'Please enter a valid name (2-50 characters, letters only)',
        email: 'Please enter a valid email address',
        phone: 'Please enter a valid phone number',
        subject: 'Please select a subject',
        message: 'Please enter your message (minimum 10 characters)'
    };

    // Real-time validation
    if (contactForm) {
        contactForm.querySelectorAll('input, select, textarea').forEach(field => {
            field.addEventListener('input', () => validateField(field));
            field.addEventListener('blur', () => validateField(field));
        });
    } else {
        console.error('Contact form element not found.');
    }

    function validateField(field) {
        const fieldName = field.getAttribute('name');
        const formGroup = field.closest('.form-group');
        const errorElement = formGroup.querySelector('.error-message');
        let isValid = true;
        let errorMessage = '';

        switch (fieldName) {
            case 'name':
                isValid = patterns.name.test(field.value);
                errorMessage = errorMessages.name;
                break;
            case 'email':
                isValid = patterns.email.test(field.value);
                errorMessage = errorMessages.email;
                break;
            case 'phone':
                if (field.value !== '') {
                    isValid = patterns.phone.test(field.value);
                    errorMessage = errorMessages.phone;
                }
                break;
            case 'subject':
                isValid = field.value !== '';
                errorMessage = errorMessages.subject;
                break;
            case 'message':
                isValid = field.value.length >= 10;
                errorMessage = errorMessages.message;
                break;
        }

        if (!isValid && field.value !== '') {
            formGroup.classList.add('error');
            errorElement.textContent = errorMessage;
        } else {
            formGroup.classList.remove('error');
            errorElement.textContent = '';
        }

        return isValid;
    }

    // Form submission
    if (contactForm) {
        contactForm.addEventListener('submit', async(e) => {
            e.preventDefault(); // Prevent default form submission

            // Validate all fields
            let isValid = true;
            contactForm.querySelectorAll('input, select, textarea').forEach(field => {
                if (!validateField(field)) {
                    isValid = false;
                }
            });

            if (!isValid) return; // This return statement is now inside the event listener callback

            // Prepare form data
            const formData = new FormData(contactForm);
            const data = Object.fromEntries(formData.entries());

            const sanitizeInput = (input) => {
                return input.replace(/[<>]/g, '');
            };
            data.message = sanitizeInput(data.message);

            try {
                // Disable submit button
                const submitBtn = contactForm.querySelector('.submit-btn');
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<span class="spinner"></span> Sending...';

                const controller = new AbortController();
                const timeoutId = setTimeout(() => controller.abort(), 10000); // 10 second timeout

                // Send form data to backend using the correct endpoint
                const response = await apiService.sendContactMessage(data);

                clearTimeout(timeoutId);

                // Show success modal
                if(successModal){
                successModal.style.display = 'block';
                successModal.setAttribute('role', 'dialog');
                successModal.setAttribute('aria-labelledby', 'modalTitle');
                }
                contactForm.reset();
            } catch (error) {
                console.error('Contact form error:', error);
                alert('Sorry, there was an error sending your message. Please try again later.');
            } finally {
                // Re-enable submit button
                const submitBtn = contactForm.querySelector('.submit-btn');
                submitBtn.disabled = false;
                submitBtn.textContent = 'Send Message';
            }
        });
    } else {
        console.error("Contact form not found!"); // This else block handles if contactForm is null
    }

    // Close modal
    if (successModal && closeModal) {
        closeModal.addEventListener('click', () => {
            successModal.style.display = 'none';
        });
    }

    // Close modal when clicking outside
    window.addEventListener('click', (e) => {
        if (e.target === successModal) {
            successModal.style.display = 'none';
        }
    });

    // Close modal when pressing Escape key
    window.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && successModal.style.display === 'block') {
            successModal.style.display = 'none';
        }
    });
});