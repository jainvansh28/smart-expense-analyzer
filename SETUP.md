# Setup Guide

## Step 1: Database Setup

### Install MySQL

Download and install MySQL 8.0+ from [mysql.com](https://dev.mysql.com/downloads/)

### Create Database

```bash
mysql -u root -p
```

```sql
CREATE DATABASE expense_analyzer;
CREATE USER 'expense_user'@'localhost' IDENTIFIED BY 'expense_pass123';
GRANT ALL PRIVILEGES ON expense_analyzer.* TO 'expense_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Run Schema

```bash
mysql -u expense_user -p expense_analyzer < database/schema.sql
```

### Generate Dummy Data

```bash
mysql -u expense_user -p expense_analyzer < database/dummy_data.sql
```

## Step 2: Backend Setup (Spring Boot)

### Navigate to backend directory

```bash
cd backend
```

### Configure application.properties

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense_analyzer
spring.datasource.username=expense_user
spring.datasource.password=expense_pass123

# Email configuration (use your Gmail)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

Backend will run on `http://localhost:8080`

## Step 3: ML Service Setup (Python FastAPI)

### Navigate to ml-service directory

```bash
cd ml-service
```

### Create virtual environment

```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

### Install dependencies

```bash
pip install -r requirements.txt
```

### Run ML service

```bash
uvicorn main:app --reload --port 8000
```

ML service will run on `http://localhost:8000`

## Step 4: Frontend Setup (React)

### Navigate to frontend directory

```bash
cd frontend
```

### Install dependencies

```bash
npm install
```

### Configure environment

Create `.env` file:

```
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_ML_API_URL=http://localhost:8000
```

### Run development server

```bash
npm start
```

Frontend will run on `http://localhost:3000`

## Step 5: Test the Application

1. Open browser to `http://localhost:3000`
2. Sign up with your email
3. Check email for OTP
4. Login and start adding expenses
5. View analytics and predictions

## Troubleshooting

### MySQL Connection Issues

- Verify MySQL is running: `sudo systemctl status mysql`
- Check credentials in application.properties
- Ensure database exists: `SHOW DATABASES;`

### Email OTP Not Sending

- Enable 2FA on Gmail
- Generate App Password: Google Account → Security → App Passwords
- Use App Password in application.properties

### ML Service Errors

- Ensure Python 3.9+ is installed
- Activate virtual environment
- Install all requirements: `pip install -r requirements.txt`

### Port Already in Use

- Backend: Change port in application.properties
- ML Service: Use `--port 8001` flag
- Frontend: Set PORT=3001 in .env

## Production Deployment

### Backend

```bash
mvn clean package
java -jar target/expense-analyzer-0.0.1-SNAPSHOT.jar
```

### ML Service

```bash
gunicorn -w 4 -k uvicorn.workers.UvicornWorker main:app
```

### Frontend

```bash
npm run build
# Serve build folder with nginx or any static server
```

## Default Test Credentials

After running dummy data script:

- Email: `test@example.com`
- Password: `Test@123`

## Support

For issues, check logs:
- Backend: `logs/spring-boot.log`
- ML Service: Console output
- Frontend: Browser console
