# Frontend Container

The frontend service hosts the browser-facing UI for the NOLA Infrastructure Reporting and Tracking System. It serves static HTML, CSS, and JavaScript from its own Nginx container and proxies requests to the auth service or the main backend API service.

## Purpose

The frontend container exists to:

- host the web UI in its own service container
- serve static HTML, CSS, and JavaScript files
- act as the main entry point for users opening the application
- proxy authentication routes to the `auth` container
- proxy application API routes to the `back-api` container

## Behavior

The webpages are hosted using Nginx inside the frontend container.

The frontend container uses a Dockerfile based on the `nginx:alpine` image. Nginx serves the static frontend files from `/usr/share/nginx/html`.

Current proxy behavior:

- `/` serves the frontend
- `/api/auth/` proxies to the `auth` service
- `/api/` proxies to the `back-api` service
- `/health` proxies to the `back-api` service
- `/db-check` proxies to the `back-api` service

## Frontend Directory Structure

The frontend service is built from the dedicated `Frontend/` directory in the repository. The frontend files were copied from `src/main/webapp` so they could be hosted independently from the backend container.

## How Frontend-to-Backend Communication Works

The frontend does not talk directly to PostgreSQL.

Instead:

- JavaScript sends HTTP requests such as `/api/auth/login`, `/api/reports`, or `/api/departments`
- Nginx proxies those requests to the correct backend service over the Docker network
- the backend service talks to PostgreSQL
- JSON responses are returned to the frontend

## Request Flow

Authentication flow:

Browser  
-> Frontend page  
-> JavaScript `fetch('/api/auth/...')`  
-> Nginx proxy  
-> `auth` container  
-> PostgreSQL  
-> JSON response  
-> frontend display

Main application flow:

Browser  
-> Frontend page  
-> JavaScript `fetch('/api/...')`  
-> Nginx proxy  
-> `back-api` container  
-> backend controller/service/repository  
-> PostgreSQL  
-> JSON response  
-> frontend display

## Commands

Build frontend image only:

`docker build -t devopps-frontend ./Frontend`

Run frontend container by itself:

`docker run --rm -p 3000:80 devopps-frontend`

Run the full project:

`docker compose up --build`

## Access Points

- Frontend: `http://localhost:3000`
- Auth direct: `http://localhost:5051`
- Backend direct: `http://localhost:5050`

Port note:

- `5051` is the host port mapped to the auth container's internal port `8080`
- `5050` is the host port mapped to the back-api container's internal port `8080`

## Run

From the project root:

`docker compose up --build`

Then go to `http://localhost:3000`.
