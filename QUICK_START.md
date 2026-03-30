# Quick Start Guide

Get the Smart Expense Analyzer running in 10 minutes!

## Prerequisites Check

```bash
# Check Java version (need 17+)
java -version

# Check Maven
mvn -version

# Check Node.js (need 18+)
node -v

# Check Python (need 3.9+)
python --version

# Check MySQL
mysql --version
```

## Step 1: Database (2 minutes)

```bash
# Start MySQL
mysql -u root -p

# Run these commands
CREATE DATABASE expense_analyzer;
CREATE USER 'expense_user'@'localhost' IDENTIFIED BY 'expense_pass123';
GRANT ALL PRIVILEGES ON expense_analyzer.* TO 'expense_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;

# Load schema and data
mysql -u expense_user -p expense_analyzer < database/schema.sql
mysql -u expense_user -p expense_analyzer < database/dummy_data.sql
```

## Step 2: Backend (2 minutes)

```bash
cd backend

# Update application.properties with your email
# Edit: src/main/resources/application.properties
# Set: spring.mail.username and spring.mail.password

# Build and run
mvn clean install
mvn spring-boot:run
```

Backend runs on: http://localhost:8080

## Step 3: ML Service (2 minutes)

```bash
cd ml-service

# Create virtual environment
python -m venv venv

# Activate (Windows)
venv\Scripts\activate

# Activate (Mac/Linux)
source venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Run service
uvicorn main:app --reload --port 8000
```

ML Service runs on: http://localhost:8000

## Step 4: Frontend (2 minutes)

```bash
cd frontend

# Install dependencies
npm install

# Create .env file
echo "REACT_APP_API_URL=http://localhost:8080/api" > .env
echo "REACT_APP_ML_API_URL=http://localhost:8000" >> .env

# Run development server
npm start
```

Frontend runs on: http://localhost:3000

## Step 5: Test It! (2 minutes)

### Option 1: Use Test Account
```
Email: test@example.com
Password: Test@123
```

### Option 2: Create New Account
1. Go to http://localhost:3000
2. Click "Sign Up"
3. Enter your email
4. Check email for OTP
5. Complete registration
6. Start adding expenses!

## Quick Commands Reference

### Start All Services

Terminal 1 - Backend:
```bash
cd backend && mvn spring-boot:run
```

Terminal 2 - ML Service:
```bash
cd ml-service && source venv/bin/activate && uvicorn main:app --reload --port 8000
```

Terminal 3 - Frontend:
```bash
cd frontend && npm start
```

### Stop All Services
- Press `Ctrl+C` in each terminal

## Common Issues & Fixes

### Port Already in Use
```bash
# Windows - Kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Mac/Linux - Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

### MySQL Connection Failed
- Check MySQL is running
- Verify credentials in application.properties
- Ensure database exists

### Email OTP Not Sending
- Use Gmail with App Password
- Enable 2FA on Google Account
- Generate App Password in Google Security settings
- Use App Password in application.properties

### Frontend Can't Connect to Backend
- Check backend is running on port 8080
- Verify .env file has correct API_URL
- Check CORS settings in SecurityConfig.java

### ML Service Not Working
- Ensure Python 3.9+ is installed
- Activate virtual environment
- Install all requirements
- Check port 8000 is free

## Testing the Features

### 1. Test Authentication
- Sign up with new email
- Verify OTP
- Login with credentials
- Check JWT token in localStorage

### 2. Test Expense Management
- Add new expense
- View in expense history
- Update expense details
- Delete expense

### 3. Test Analytics
- View dashboard
- Check monthly totals
- See category distribution
- Review suggestions

### 4. Test ML Predictions
- Click "Generate Prediction"
- View predicted expense
- Check overspending risk
- See savings prediction

## API Testing with cURL

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test@123"}'
```

### Add Expense
```bash
curl -X POST http://localhost:8080/api/expense/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"amount":500,"category":"Food","description":"Lunch","date":"2024-01-15"}'
```

### Get Analytics
```bash
curl -X GET http://localhost:8080/api/analytics/monthly \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Development Tips

### Hot Reload
- Frontend: Automatic with `npm start`
- Backend: Use Spring DevTools or restart
- ML Service: Automatic with `--reload` flag

### View Logs
- Backend: Console output or logs/spring-boot.log
- ML Service: Console output
- Frontend: Browser console (F12)

### Database GUI
Use MySQL Workbench or phpMyAdmin to view data:
```
Host: localhost
Port: 3306
Database: expense_analyzer
Username: expense_user
Password: expense_pass123
```

## Next Steps

1. ✅ Explore the dashboard
2. ✅ Add your real expenses
3. ✅ Generate predictions
4. ✅ Review smart suggestions
5. ✅ Customize categories
6. ✅ Export data (feature ready)
7. ✅ Set budget goals (feature ready)

## Production Deployment

### Backend
```bash
cd backend
mvn clean package
java -jar target/expense-analyzer-0.0.1-SNAPSHOT.jar
```

### ML Service
```bash
cd ml-service
pip install gunicorn
gunicorn -w 4 -k uvicorn.workers.UvicornWorker main:app
```

### Frontend
```bash
cd frontend
npm run build
# Serve build folder with nginx or any static server
```

## Support

- 📖 Full documentation: See README.md
- 🔧 API docs: See API.md
- 🏗️ Architecture: See PROJECT_STRUCTURE.md
- ✨ Features: See FEATURES.md
- ⚙️ Setup: See SETUP.md

## Success Checklist

- [ ] MySQL database created and populated
- [ ] Backend running on port 8080
- [ ] ML service running on port 8000
- [ ] Frontend running on port 3000
- [ ] Can access landing page
- [ ] Can sign up and receive OTP
- [ ] Can login successfully
- [ ] Can add expenses
- [ ] Can view dashboard with charts
- [ ] Can generate predictions

If all checked, you're ready to go! 🚀
