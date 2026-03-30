# Deployment Guide

## Production Deployment Options

### Option 1: Traditional Server Deployment

#### Backend (Spring Boot)

1. **Build the application**
```bash
cd backend
mvn clean package -DskipTests
```

2. **Configure production properties**
Create `application-prod.properties`:
```properties
server.port=8080
spring.datasource.url=jdbc:mysql://your-db-host:3306/expense_analyzer
spring.datasource.username=your-db-user
spring.datasource.password=your-db-password
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
jwt.secret=your-production-secret-key-min-256-bits
ml.service.url=http://your-ml-service-url:8000
cors.allowed.origins=https://your-frontend-domain.com
```

3. **Run with production profile**
```bash
java -jar -Dspring.profiles.active=prod target/expense-analyzer-0.0.1-SNAPSHOT.jar
```

4. **Setup as systemd service** (Linux)
Create `/etc/systemd/system/expense-analyzer.service`:
```ini
[Unit]
Description=Expense Analyzer Backend
After=syslog.target network.target

[Service]
User=your-user
ExecStart=/usr/bin/java -jar /path/to/expense-analyzer-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable expense-analyzer
sudo systemctl start expense-analyzer
```

#### ML Service (FastAPI)

1. **Install production server**
```bash
cd ml-service
pip install gunicorn uvicorn[standard]
```

2. **Run with Gunicorn**
```bash
gunicorn -w 4 -k uvicorn.workers.UvicornWorker main:app --bind 0.0.0.0:8000
```

3. **Setup as systemd service**
Create `/etc/systemd/system/ml-service.service`:
```ini
[Unit]
Description=ML Prediction Service
After=network.target

[Service]
User=your-user
WorkingDirectory=/path/to/ml-service
ExecStart=/path/to/venv/bin/gunicorn -w 4 -k uvicorn.workers.UvicornWorker main:app --bind 0.0.0.0:8000
Restart=always

[Install]
WantedBy=multi-user.target
```

#### Frontend (React)

1. **Build for production**
```bash
cd frontend
npm run build
```

2. **Serve with Nginx**
Install Nginx and configure:
```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /path/to/frontend/build;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}
```

3. **Enable HTTPS with Let's Encrypt**
```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

### Option 2: Docker Deployment

#### Create Dockerfiles

**Backend Dockerfile** (`backend/Dockerfile`):
```dockerfile
FROM maven:3.8-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**ML Service Dockerfile** (`ml-service/Dockerfile`):
```dockerfile
FROM python:3.9-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
EXPOSE 8000
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
```

**Frontend Dockerfile** (`frontend/Dockerfile`):
```dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### Docker Compose

Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpass
      MYSQL_DATABASE: expense_analyzer
      MYSQL_USER: expense_user
      MYSQL_PASSWORD: expense_pass123
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database/schema.sql:/docker-entrypoint-initdb.d/1-schema.sql
      - ./database/dummy_data.sql:/docker-entrypoint-initdb.d/2-data.sql

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/expense_analyzer
      SPRING_DATASOURCE_USERNAME: expense_user
      SPRING_DATASOURCE_PASSWORD: expense_pass123
      ML_SERVICE_URL: http://ml-service:8000
    depends_on:
      - mysql
      - ml-service

  ml-service:
    build: ./ml-service
    ports:
      - "8000:8000"

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

**Deploy with Docker Compose**:
```bash
docker-compose up -d
```

### Option 3: Cloud Deployment

#### AWS Deployment

**Backend (Elastic Beanstalk)**:
1. Package application: `mvn clean package`
2. Create Elastic Beanstalk application
3. Upload JAR file
4. Configure environment variables
5. Setup RDS MySQL instance
6. Configure security groups

**ML Service (EC2 or Lambda)**:
- EC2: Deploy as systemd service
- Lambda: Package with AWS Lambda Python runtime

**Frontend (S3 + CloudFront)**:
1. Build: `npm run build`
2. Upload to S3 bucket
3. Enable static website hosting
4. Setup CloudFront distribution
5. Configure custom domain

#### Heroku Deployment

**Backend**:
```bash
cd backend
heroku create expense-analyzer-backend
heroku addons:create cleardb:ignite
git push heroku main
```

**ML Service**:
```bash
cd ml-service
heroku create expense-analyzer-ml
echo "web: uvicorn main:app --host 0.0.0.0 --port $PORT" > Procfile
git push heroku main
```

**Frontend**:
```bash
cd frontend
heroku create expense-analyzer-frontend
heroku buildpacks:set mars/create-react-app
git push heroku main
```

#### DigitalOcean App Platform

1. Connect GitHub repository
2. Configure build settings
3. Set environment variables
4. Deploy with one click

### Database Migration

#### Production Database Setup

```sql
-- Create production database
CREATE DATABASE expense_analyzer_prod;

-- Create user with limited privileges
CREATE USER 'expense_prod'@'%' IDENTIFIED BY 'strong-password-here';
GRANT SELECT, INSERT, UPDATE, DELETE ON expense_analyzer_prod.* TO 'expense_prod'@'%';
FLUSH PRIVILEGES;

-- Run schema
USE expense_analyzer_prod;
SOURCE schema.sql;
```

#### Backup Strategy

**Automated Daily Backups**:
```bash
#!/bin/bash
# backup.sh
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u expense_prod -p expense_analyzer_prod > backup_$DATE.sql
# Upload to S3 or backup server
aws s3 cp backup_$DATE.sql s3://your-backup-bucket/
```

Add to crontab:
```bash
0 2 * * * /path/to/backup.sh
```

### Environment Variables

#### Backend (.env or system environment)
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/db
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=pass
SPRING_MAIL_USERNAME=email@gmail.com
SPRING_MAIL_PASSWORD=app-password
JWT_SECRET=your-secret-key
ML_SERVICE_URL=http://ml-service:8000
CORS_ALLOWED_ORIGINS=https://your-domain.com
```

#### Frontend (.env.production)
```bash
REACT_APP_API_URL=https://api.your-domain.com/api
REACT_APP_ML_API_URL=https://ml.your-domain.com
```

### SSL/TLS Configuration

#### Nginx with Let's Encrypt
```bash
sudo certbot --nginx -d your-domain.com -d www.your-domain.com
```

#### Spring Boot SSL (Optional)
```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=your-password
server.ssl.key-store-type=PKCS12
```

### Monitoring & Logging

#### Application Monitoring

**Spring Boot Actuator**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Enable endpoints:
```properties
management.endpoints.web.exposure.include=health,info,metrics
```

#### Log Aggregation

**Logback configuration** (`logback-spring.xml`):
```xml
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

### Performance Optimization

#### Database Optimization
```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_user_date ON expenses(user_id, date);
CREATE INDEX idx_category ON expenses(category);
CREATE INDEX idx_email ON users(email);
```

#### Caching (Redis)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### Connection Pooling
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

### Security Checklist

- [ ] Change default passwords
- [ ] Use strong JWT secret (256+ bits)
- [ ] Enable HTTPS/SSL
- [ ] Configure CORS properly
- [ ] Set up firewall rules
- [ ] Enable rate limiting
- [ ] Regular security updates
- [ ] Database backups
- [ ] Monitor logs for suspicious activity
- [ ] Use environment variables for secrets

### Scaling Strategies

#### Horizontal Scaling
- Load balancer (Nginx, HAProxy)
- Multiple backend instances
- Shared database
- Redis for session management

#### Vertical Scaling
- Increase server resources
- Optimize database queries
- Add caching layer
- CDN for static assets

### Cost Optimization

#### Free Tier Options
- **Backend**: Heroku free tier, Railway
- **Database**: ElephantSQL, PlanetScale
- **ML Service**: Render, Railway
- **Frontend**: Vercel, Netlify, GitHub Pages
- **Domain**: Freenom (free domains)

#### Budget-Friendly Options
- **DigitalOcean**: $5/month droplet
- **AWS Lightsail**: $3.50/month
- **Linode**: $5/month
- **Vultr**: $2.50/month

### Maintenance

#### Regular Tasks
- Weekly: Check logs for errors
- Monthly: Update dependencies
- Quarterly: Security audit
- Yearly: Review and optimize

#### Update Strategy
```bash
# Backend
mvn versions:display-dependency-updates

# Frontend
npm outdated
npm update

# ML Service
pip list --outdated
pip install --upgrade package-name
```

### Rollback Plan

1. Keep previous version JAR/build
2. Database migration rollback scripts
3. Quick deployment script
4. Health check endpoints
5. Automated testing before deployment

### Support & Troubleshooting

#### Health Check Endpoints
- Backend: `http://your-domain:8080/actuator/health`
- ML Service: `http://your-domain:8000/health`
- Frontend: Check if page loads

#### Common Issues
- Database connection: Check credentials and network
- CORS errors: Verify allowed origins
- 502 Bad Gateway: Backend not running
- Email not sending: Check SMTP settings

### Success Metrics

Monitor these KPIs:
- Response time < 200ms
- Uptime > 99.9%
- Error rate < 0.1%
- Database query time < 50ms
- User satisfaction score

Your application is now production-ready! 🚀
