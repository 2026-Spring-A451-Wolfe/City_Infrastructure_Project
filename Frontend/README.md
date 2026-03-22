# Frontend Container

The frontend service is responsible for hosting the web-based user interface for the NOLA Infrastructure Reporting & Tracking System. Its purpose is to serve the application’s static frontend assets, including HTML, CSS, JavaScript, and any related media files, from its own dedicated container.

The frontend container does not connect directly to the database. Instead, the webpages communicate with backend API endpoints, and the backend service handles all database access through its controller, service, and repository layers.

## Purpose
The frontend container exists to:
* host the web UI in its own service container
* serve static HTML, CSS, and JavaScript files
* act as the main entry point for users opening the application
* route API requests to the backend container through Docker networking
* support a cleaner project-tier Docker environment instead of the earlier demo structure
## Behavior
The webpages are hosted using nginx inside the frontend container.

The frontend container uses a Dockerfile based on the nginx:alpine image. nginx serves the static frontend files from /usr/share/nginx/html. This is appropriate because the frontend is made up of static assets and does not need to execute Java code. Tomcat remains the application server for the Java backend, while nginx serves as the frontend web server and reverse proxy.

- Opening `/` redirects to `login-page.html`
- `/api`, and `/db-check` are proxied to the backend API `back-api` service, while `/health` is proxied to the main back `tomcat` service. 

## Frontend Directory Structure

The frontend service is built from a dedicated Frontend/ directory in the repository. The frontend files were copied from src/main/webapp so they could be hosted independently of the backend Tomcat application.

## How Frontend-to-Backend Communication Works

The frontend does not talk directly to PostgreSQL. Instead, JavaScript on the pages sends HTTP requests to backend API endpoints such as /api/auth/login, /api/reports, or /api/departments. nginx then proxies those requests to the backend container using the backend service name defined in Docker Compose.

## Request flow

Browser
→ Frontend page
→ JavaScript fetch('/api/...')
→ nginx proxy
→ backend container (Tomcat)
→ backend controller/service/repository
→ PostgreSQL
→ JSON response
→ frontend display

## Commands

Build frontend image only: docker build -t devopps-frontend ./Frontend

Run frontend container by itself: docker run --rm -p 3000:80 devopps-frontend

Run the full project: docker compose up --build

Access points: 
- Frontend: http://localhost:3000
- Backend direct: http://localhost:5050
- Tomcat direct: http://localhost:8080


## Run
From the project root:

docker compose up --build

Then go to http://localhost:3000
