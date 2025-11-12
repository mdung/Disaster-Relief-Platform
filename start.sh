#!/bin/bash

echo "Starting Disaster Relief Platform..."
echo

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is not installed or not in PATH"
    echo "Please install Docker from https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "ERROR: Docker Compose is not installed or not in PATH"
    echo "Please install Docker Compose from https://docs.docker.com/compose/install/"
    exit 1
fi

echo "Building and starting services..."
docker-compose up --build -d

echo
echo "Waiting for services to start..."
sleep 30

echo
echo "Checking service status..."
docker-compose ps

echo
echo "========================================"
echo "Disaster Relief Platform is starting!"
echo "========================================"
echo
echo "Services:"
echo "- Frontend: http://localhost:3000"
echo "- Backend API: http://localhost:8080"
echo "- API Docs: http://localhost:8080/swagger-ui.html"
echo "- MinIO Console: http://localhost:9001"
echo "- Prometheus: http://localhost:9090 (if monitoring enabled)"
echo "- Grafana: http://localhost:3001 (if monitoring enabled)"
echo
echo "Default admin credentials:"
echo "- Email: admin@relief.local"
echo "- Password: admin123"
echo
echo "MinIO credentials:"
echo "- Username: minioadmin"
echo "- Password: minioadmin123"
echo
echo "To stop the platform, run: docker-compose down"
echo "To view logs, run: docker-compose logs -f"
echo



