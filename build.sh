#!/bin/bash

# Build script for NOLA Infrastructure Reporting & Tracking System
# This script compiles the Java project and builds/runs the Docker services

set -e  # Exit on any error

echo "Starting build process for NOLA Infrastructure Reporting & Tracking System"
echo "=============================================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is running
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        print_error "Docker daemon is not running. Please start Docker Desktop and try again."
        exit 1
    fi
    print_success "Docker daemon is running"
}

# Check if required files exist
check_requirements() {
    if [ ! -f "pom.xml" ]; then
        print_error "pom.xml not found. Are you in the project root directory?"
        exit 1
    fi

    if [ ! -f "docker-compose.yml" ]; then
        print_error "docker-compose.yml not found."
        exit 1
    fi

    if [ ! -f ".env" ]; then
        print_error ".env file not found. Please create it with database and JWT configuration."
        exit 1
    fi

    print_success "All required files found"
}

# Clean previous build artifacts
clean_project() {
    print_status "Cleaning previous build artifacts..."
    mvn clean >/dev/null 2>&1
    print_success "Clean completed"
}

# Compile and package the Java project
build_java() {
    print_status "Compiling and packaging Java project..."
    if mvn package -DskipTests; then
        print_success "Java build completed successfully"
    else
        print_error "Java build failed"
        exit 1
    fi
}

# Build and start Docker services
build_docker() {
    print_status "Building and starting Docker services..."
    print_warning "This may take several minutes on first run..."

    if docker-compose up --build -d; then
        print_success "Docker services started successfully"
    else
        print_error "Failed to start Docker services"
        exit 1
    fi
}

# Wait for services to be healthy
wait_for_services() {
    print_status "Waiting for services to be ready..."

    # Wait for database to be healthy
    print_status "Waiting for database..."
    timeout=60
    while [ $timeout -gt 0 ]; do
        if docker-compose ps db | grep -q "healthy"; then
            print_success "Database is ready"
            break
        fi
        sleep 2
        timeout=$((timeout - 2))
    done

    if [ $timeout -le 0 ]; then
        print_warning "Database health check timed out, but continuing..."
    fi

    # Wait for Tomcat to be ready
    print_status "Waiting for Tomcat application..."
    sleep 10  # Give Tomcat time to deploy the application

    print_success "Services should now be ready"
}

# Display service information
show_services() {
    echo ""
    echo "Build completed successfully!"
    echo "=================================="
    echo ""
    echo "Services are running on:"
    echo "   Frontend:    http://localhost:3000"
    echo "   Backend API: http://localhost:8080"
    echo "   Database:    localhost:5433 (internal)"
    echo ""
    echo "To view service status: docker-compose ps"
    echo "To view logs:           docker-compose logs -f [service-name]"
    echo "To stop services:       docker-compose down"
    echo ""
}

# Main build process
main() {
    echo "Building NOLA Infrastructure Reporting & Tracking System"
    echo ""

    check_docker
    check_requirements
    clean_project
    build_java
    build_docker
    wait_for_services
    show_services
}

# Handle command line arguments
case "${1:-}" in
    "clean")
        print_status "Cleaning project..."
        clean_project
        docker-compose down -v 2>/dev/null || true
        print_success "Project cleaned"
        ;;
    "java-only")
        print_status "Building Java project only..."
        check_requirements
        clean_project
        build_java
        print_success "Java build completed"
        ;;
    "docker-only")
        print_status "Building Docker services only..."
        check_docker
        check_requirements
        build_docker
        wait_for_services
        show_services
        ;;
    "stop")
        print_status "Stopping all services..."
        docker-compose down
        print_success "Services stopped"
        ;;
    "restart")
        print_status "Restarting services..."
        docker-compose restart
        wait_for_services
        show_services
        ;;
    "logs")
        if [ -n "$2" ]; then
            docker-compose logs -f "$2"
        else
            docker-compose logs -f
        fi
        ;;
    "status")
        docker-compose ps
        ;;
    *)
        main
        ;;
esac