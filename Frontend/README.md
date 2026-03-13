# Frontend Container

## Purpose
This container serves the frontend HTML, CSS, and JavaScript using nginx.

## Behavior
- Opening `/` redirects to `login-page.html`
- `/api`, `/health`, and `/db-check` are proxied to the backend `tomcat` service

## Run
From the project root:

docker compose up --build

Then go to http://localhost:3000
