# Build Script Usage

This shell script (`build.sh`) automates the build and deployment process for the NOLA Infrastructure Reporting & Tracking System.

## Usage

### Full Build (Default)
```bash
./build.sh
```
Compiles the Java project, builds Docker images, and starts all services.

### Specific Commands
- **Clean build**: `./build.sh clean` - Cleans build artifacts and stops/removes containers
- **Java only**: `./build.sh java-only` - Compiles and packages Java project only
- **Docker only**: `./build.sh docker-only` - Builds and starts Docker services only
- **Stop services**: `./build.sh stop` - Stops all running services
- **Restart services**: `./build.sh restart` - Restarts all services
- **View logs**: `./build.sh logs [service-name]` - Shows logs (optionally for specific service)
- **Service status**: `./build.sh status` - Shows status of all services

## Prerequisites

- Docker Desktop running
- Maven installed (for Java builds)
- All required files present (pom.xml, docker-compose.yml, .env)

## Services Started

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: localhost:5433 (internal access only)

## Troubleshooting

If the build fails:
1. Ensure Docker Desktop is running
2. Check that all required files exist
3. Try `./build.sh clean` then `./build.sh` again
4. Check logs with `./build.sh logs` for detailed error information