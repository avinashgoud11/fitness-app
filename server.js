// const express = require('express');
// const cors = require('cors');
// const rateLimit = require('express-rate-limit');
// const helmet = require('helmet');
// const app = express();

// // Security middleware
// app.use(helmet());
// app.use(cors({
//     origin: 'https://legendary-ganache-a2dcf3.netlify.app/', // Update this with your frontend URL
//     methods: ['POST'],
//     allowedHeaders: ['Content-Type']
// }));
// app.use(express.json());

// // Rate limiting
// const limiter = rateLimit({
//     windowMs: 15 * 60 * 1000, // 15 minutes
//     max: 5 // limit each IP to 5 requests per windowMs
// });

// // Contact form endpoint
// app.post('/api/contact', limiter, async(req, res) => {
//     try {
//         const { name, email, phone, subject, message } = req.body;

//         // Input validation
//         if (!name || !email || !subject || !message) {
//             return res.status(400).json({ message: 'All required fields must be filled' });
//         }

//         // Email validation
//         const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
//         if (!emailRegex.test(email)) {
//             return res.status(400).json({ message: 'Invalid email format' });
//         }

//         // Phone validation (optional field)
//         if (phone) {
//             const phoneRegex = /^[\d\s-+()]{10,15}$/;
//             if (!phoneRegex.test(phone)) {
//                 return res.status(400).json({ message: 'Invalid phone number format' });
//             }
//         }

//         // Here you would typically:
//         // 1. Save to database
//         // 2. Send email notification
//         // 3. Log the contact request

//         // For now, we'll just simulate a successful submission
//         res.status(200).json({ message: 'Message sent successfully' });
//     } catch (error) {
//         console.error('Contact form error:', error);
//         res.status(500).json({ message: 'Internal server error' });
//     }
// });

// const PORT = process.env.PORT || 5000;
// app.listen(PORT, () => {
//     console.log(`Server running on port ${PORT}`);
// });