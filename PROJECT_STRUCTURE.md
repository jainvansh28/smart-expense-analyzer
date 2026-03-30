# Project Structure

```
smart-expense-analyzer/
│
├── README.md                          # Project overview
├── SETUP.md                           # Detailed setup instructions
├── API.md                             # Complete API documentation
├── .gitignore                         # Git ignore file
│
├── database/                          # Database scripts
│   ├── schema.sql                     # Database schema
│   └── dummy_data.sql                 # 400+ dummy expense records
│
├── backend/                           # Spring Boot Backend
│   ├── pom.xml                        # Maven dependencies
│   └── src/
│       ├── main/
│       │   ├── java/com/expenseanalyzer/
│       │   │   ├── ExpenseAnalyzerApplication.java    # Main application
│       │   │   ├── model/                             # Entity models
│       │   │   │   ├── User.java
│       │   │   │   ├── Expense.java
│       │   │   │   ├── OtpVerification.java
│       │   │   │   └── Prediction.java
│       │   │   ├── repository/                        # JPA repositories
│       │   │   │   ├── UserRepository.java
│       │   │   │   ├── ExpenseRepository.java
│       │   │   │   ├── OtpRepository.java
│       │   │   │   └── PredictionRepository.java
│       │   │   ├── dto/                               # Data Transfer Objects
│       │   │   │   ├── SignupRequest.java
│       │   │   │   ├── LoginRequest.java
│       │   │   │   ├── AuthResponse.java
│       │   │   │   ├── ExpenseRequest.java
│       │   │   │   └── AnalyticsResponse.java
│       │   │   ├── service/                           # Business logic
│       │   │   │   ├── AuthService.java
│       │   │   │   ├── EmailService.java
│       │   │   │   ├── ExpenseService.java
│       │   │   │   ├── AnalyticsService.java
│       │   │   │   └── MLServiceClient.java
│       │   │   ├── controller/                        # REST controllers
│       │   │   │   ├── AuthController.java
│       │   │   │   ├── ExpenseController.java
│       │   │   │   ├── AnalyticsController.java
│       │   │   │   └── PredictionController.java
│       │   │   └── security/                          # Security configuration
│       │   │       ├── JwtUtil.java
│       │   │       ├── JwtAuthenticationFilter.java
│       │   │       └── SecurityConfig.java
│       │   └── resources/
│       │       └── application.properties             # Configuration
│       └── test/                                      # Test files
│
├── ml-service/                        # Python FastAPI ML Service
│   ├── requirements.txt               # Python dependencies
│   └── main.py                        # ML service with prediction logic
│
└── frontend/                          # React Frontend
    ├── package.json                   # NPM dependencies
    ├── tailwind.config.js             # Tailwind CSS configuration
    ├── public/
    │   └── index.html                 # HTML template
    └── src/
        ├── index.js                   # React entry point
        ├── index.css                  # Global styles
        ├── App.js                     # Main app component
        ├── context/
        │   └── AuthContext.js         # Authentication context
        ├── services/
        │   └── api.js                 # API service layer
        └── pages/
            ├── LandingPage.js         # Landing page with animations
            ├── SignupPage.js          # Signup with OTP verification
            ├── LoginPage.js           # Login page
            ├── DashboardPage.js       # Main dashboard with charts
            ├── AddExpensePage.js      # Add expense form
            ├── ExpenseHistoryPage.js  # Expense list
            └── ProfilePage.js         # User profile
```

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security with JWT
- Spring Data JPA
- MySQL 8.0
- JavaMail for OTP
- BCrypt for password hashing

### ML Service
- Python 3.9+
- FastAPI
- Scikit-learn (Linear Regression)
- Pandas & NumPy
- Uvicorn

### Frontend
- React 18
- React Router DOM
- Axios for API calls
- Framer Motion for animations
- Recharts for data visualization
- Lucide React for icons
- Tailwind CSS for styling
- date-fns for date formatting

## Features Implemented

### Authentication
- ✅ Email/Password signup
- ✅ OTP verification via email
- ✅ JWT-based authentication
- ✅ BCrypt password hashing
- ✅ Secure token storage

### Expense Management
- ✅ Add expense
- ✅ Update expense
- ✅ Delete expense
- ✅ View expense history
- ✅ Category-based organization

### Analytics
- ✅ Monthly total spending
- ✅ Category-wise distribution
- ✅ Month-over-month comparison
- ✅ Financial health score (0-100)
- ✅ Savings estimation
- ✅ Smart suggestions

### Machine Learning
- ✅ Linear regression model
- ✅ Next month expense prediction
- ✅ Overspending risk calculation
- ✅ Savings prediction
- ✅ Historical data analysis

### UI/UX
- ✅ Modern fintech design
- ✅ Gradient backgrounds
- ✅ Glassmorphism effects
- ✅ Framer Motion animations
- ✅ Responsive design
- ✅ Interactive charts (Pie & Line)
- ✅ Smooth transitions
- ✅ Loading states

### Smart Features
- ✅ Rule-based suggestions
- ✅ Student budget mode ready
- ✅ Category spending alerts
- ✅ Trend analysis

## Database Schema

### users
- id (PK)
- name
- email (unique)
- password (hashed)
- created_at

### expenses
- id (PK)
- user_id (FK)
- amount
- category
- description
- date
- created_at

### otp_verification
- id (PK)
- email
- otp
- created_at
- expires_at
- verified

### predictions
- id (PK)
- user_id (FK)
- predicted_expense
- overspending_risk_percentage
- savings_prediction
- prediction_date
- created_at

### user_preferences
- id (PK)
- user_id (FK)
- dark_mode
- student_mode
- monthly_budget
- created_at
- updated_at

## API Endpoints

### Authentication
- POST /api/auth/send-otp
- POST /api/auth/verify-otp
- POST /api/auth/signup
- POST /api/auth/login

### Expenses
- POST /api/expense/add
- PUT /api/expense/update/{id}
- DELETE /api/expense/delete/{id}
- GET /api/expense/list
- GET /api/expense/{id}

### Analytics
- GET /api/analytics/monthly

### Predictions
- GET /api/prediction/next-month
- GET /api/prediction/latest

## Security Features

- JWT token-based authentication
- BCrypt password hashing
- CORS configuration
- SQL injection prevention (JPA)
- XSS protection
- Secure HTTP headers
- Token expiration (24 hours)

## Performance Optimizations

- Database indexing on frequently queried columns
- Connection pooling
- Lazy loading for JPA entities
- React component memoization
- Code splitting
- Optimized bundle size

## Future Enhancements

- Dark mode toggle
- Export to CSV
- Budget setting
- Recurring expenses
- Multi-currency support
- Mobile app
- Email notifications
- Advanced ML models (LSTM, Prophet)
- Social features
- Bill reminders
