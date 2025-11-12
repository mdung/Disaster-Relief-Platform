@echo off
echo Starting Disaster Relief Platform...
echo.

echo Checking Docker installation...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker is not installed or not in PATH
    echo Please install Docker Desktop from https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

echo Checking Docker Compose...
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker Compose is not installed or not in PATH
    echo Please install Docker Compose
    pause
    exit /b 1
)

echo.
echo Building and starting services...
docker-compose up --build -d

echo.
echo Waiting for services to start...
timeout /t 30 /nobreak >nul

echo.
echo Checking service status...
docker-compose ps

echo.
echo ========================================
echo Disaster Relief Platform is starting!
echo ========================================
echo.
echo Services:
echo - Frontend: http://localhost:3000
echo - Backend API: http://localhost:8080
echo - API Docs: http://localhost:8080/swagger-ui.html
echo - MinIO Console: http://localhost:9001
echo - Prometheus: http://localhost:9090 (if monitoring enabled)
echo - Grafana: http://localhost:3001 (if monitoring enabled)
echo.
echo Default admin credentials:
echo - Email: admin@relief.local
echo - Password: admin123
echo.
echo MinIO credentials:
echo - Username: minioadmin
echo - Password: minioadmin123
echo.
echo To stop the platform, run: docker-compose down
echo To view logs, run: docker-compose logs -f
echo.
pause



