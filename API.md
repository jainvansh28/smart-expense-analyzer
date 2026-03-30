# API Documentation

Base URL: `http://localhost:8080/api`

## Authentication Endpoints

### Send OTP
```
POST /auth/send-otp
Content-Type: application/json

Request:
{
  "email": "user@example.com"
}

Response:
{
  "message": "OTP sent successfully"
}
```

### Verify OTP
```
POST /auth/verify-otp
Content-Type: application/json

Request:
{
  "email": "user@example.com",
  "otp": "123456"
}

Response:
{
  "message": "OTP verified successfully"
}
```

### Signup
```
POST /auth/signup
Content-Type: application/json

Request:
{
  "name": "John Doe",
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "name": "John Doe",
  "email": "user@example.com"
}
```

### Login
```
POST /auth/login
Content-Type: application/json

Request:
{
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "name": "John Doe",
  "email": "user@example.com"
}
```

## Expense Endpoints

All expense endpoints require JWT authentication.
Add header: `Authorization: Bearer <token>`

### Add Expense
```
POST /expense/add
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "amount": 500.00,
  "category": "Food",
  "description": "Grocery shopping",
  "date": "2024-01-15"
}

Response:
{
  "id": 1,
  "userId": 1,
  "amount": 500.00,
  "category": "Food",
  "description": "Grocery shopping",
  "date": "2024-01-15",
  "createdAt": "2024-01-15T10:30:00"
}
```

### Update Expense
```
PUT /expense/update/{id}
Authorization: Bearer <token>
Content-Type: application/json

Request:
{
  "amount": 550.00,
  "category": "Food",
  "description": "Updated grocery shopping",
  "date": "2024-01-15"
}

Response:
{
  "id": 1,
  "userId": 1,
  "amount": 550.00,
  "category": "Food",
  "description": "Updated grocery shopping",
  "date": "2024-01-15",
  "createdAt": "2024-01-15T10:30:00"
}
```

### Delete Expense
```
DELETE /expense/delete/{id}
Authorization: Bearer <token>

Response:
{
  "message": "Expense deleted successfully"
}
```

### List All Expenses
```
GET /expense/list
Authorization: Bearer <token>

Response:
[
  {
    "id": 1,
    "userId": 1,
    "amount": 500.00,
    "category": "Food",
    "description": "Grocery shopping",
    "date": "2024-01-15",
    "createdAt": "2024-01-15T10:30:00"
  },
  ...
]
```

### Get Expense by ID
```
GET /expense/{id}
Authorization: Bearer <token>

Response:
{
  "id": 1,
  "userId": 1,
  "amount": 500.00,
  "category": "Food",
  "description": "Grocery shopping",
  "date": "2024-01-15",
  "createdAt": "2024-01-15T10:30:00"
}
```

## Analytics Endpoints

### Get Monthly Analytics
```
GET /analytics/monthly?year=2024&month=1
Authorization: Bearer <token>

Query Parameters:
- year (optional): Year for analytics (default: current year)
- month (optional): Month for analytics (default: current month)

Response:
{
  "monthlyTotal": 25000.00,
  "categoryWiseSpending": {
    "Food": 5000.00,
    "Travel": 3000.00,
    "Shopping": 8000.00,
    "Bills": 6000.00,
    "Entertainment": 3000.00
  },
  "categoryPercentages": {
    "Food": 20.0,
    "Travel": 12.0,
    "Shopping": 32.0,
    "Bills": 24.0,
    "Entertainment": 12.0
  },
  "previousMonthTotal": 22000.00,
  "monthOverMonthChange": 13.64,
  "estimatedSavings": 25000.00,
  "financialHealthScore": 75,
  "suggestions": [
    "Your spending increased by 13.6% this month. Consider reviewing your expenses.",
    "Shopping expenses are high. Try to limit non-essential purchases."
  ]
}
```

## Prediction Endpoints

### Get Next Month Prediction
```
GET /prediction/next-month
Authorization: Bearer <token>

Response:
{
  "id": 1,
  "userId": 1,
  "predictedExpense": 26500.00,
  "overspendingRiskPercentage": 65,
  "savingsPrediction": 23500.00,
  "predictionDate": "2024-02-01",
  "createdAt": "2024-01-20T10:30:00"
}
```

### Get Latest Prediction
```
GET /prediction/latest
Authorization: Bearer <token>

Response:
{
  "id": 1,
  "userId": 1,
  "predictedExpense": 26500.00,
  "overspendingRiskPercentage": 65,
  "savingsPrediction": 23500.00,
  "predictionDate": "2024-02-01",
  "createdAt": "2024-01-20T10:30:00"
}
```

## Error Responses

All endpoints may return error responses in the following format:

```json
{
  "error": "Error message description"
}
```

Common HTTP Status Codes:
- 200: Success
- 400: Bad Request (validation error)
- 401: Unauthorized (invalid or missing token)
- 404: Not Found
- 500: Internal Server Error

## Categories

Valid expense categories:
- Food
- Travel
- Shopping
- Bills
- Entertainment
- Other
