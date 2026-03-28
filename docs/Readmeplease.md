Tomcat + PostgreSQL Docker Demo

Table of Contents
1. Project overview
2. Setup and run
3. Front End team guide
4. Back End team guide
5. QA team guide
6. Common issues
7. HELP MY FIRST TIME GIT COMMITING HERE!!
8. Database Management

1) Project overview

This project runs a Java servlet app in Tomcat and a PostgreSQL database in Docker.
There are two containers in this stack: `db` (PostgreSQL) and `back-api` (Tomcat + static UI and servlets from `src/main/webapp`).
DataGrip is the database client for this project. It is not a container.

How requests flow:
Browser -> Tomcat servlet -> PostgreSQL

2) Setup and run

Requirements:
- Docker Desktop running

Create a .env file in the repo root with:

DB_NAME=demo
DB_USER=demo
DB_PASSWORD=demo
DB_HOST_PORT=55432

Run from repo root:

docker compose down -v --remove-orphans
docker compose up --build -d
docker compose ps

Expected result:
- db is Up (healthy)
- back-api is Up

App URL (Tomcat maps host 5050 to container 8080):
- http://localhost:5050

Important connection rule:
- App container connects to DB using db:5432 (internal Docker network)
- DataGrip connects from your machine using localhost:<DB_HOST_PORT>

3) Front End team guide

Where to edit web UI files (served by Tomcat from the WAR, no separate web container):
- `src/main/webapp/` (HTML, CSS, JS, `WEB-INF/web.xml`)

What to do:
1. Edit HTML/CSS/JS in `src/main/webapp`
2. Rebuild and run: `docker compose up --build -d`
3. Open http://localhost:5050 and verify the page

Before push:
- Confirm page loads in browser
- Confirm container status with docker compose ps
- Commit with a clear message, then push your branch

Push flow:
- git checkout -b feature/<name>
- git add .
- git commit -m "feat: update frontend page"
- git push -u origin feature/<name>

4) Back End team guide

Where to edit backend files:
- Servlets: src/main/java/com/example/web
- Tests: src/test/java/com/example/web

Current endpoints:
- /health returns OK
- /db-check checks DB connection and returns DB OK or DB FAIL

DataGrip connection settings:
- Host: localhost
- Port: value in .env for DB_HOST_PORT (currently 55432)
- Database: value in .env for DB_NAME
- User: value in .env for DB_USER
- Password: value in .env for DB_PASSWORD

Build/test cycle:
- mvn -DskipTests package
- mvn test
- docker compose up --build -d

Before push:
- Endpoint checks:
  - http://localhost:5050/health
  - http://localhost:5050/db-check
- Tests pass with mvn test

5) QA team guide

How to run for testing:
1. docker compose down -v --remove-orphans
2. docker compose up --build -d
3. docker compose ps

What to test:
- App home page loads: http://localhost:5050
- Health endpoint returns OK: http://localhost:5050/health
- DB endpoint returns DB OK: http://localhost:5050/db-check

If /db-check fails:
- Confirm db is healthy in docker compose ps
- Confirm .env values are correct
- Confirm no other local process is taking the DB host port

6) Common issues

Port already in use:
- Change DB_HOST_PORT in .env (example 55433)
- Restart: docker compose down -v --remove-orphans then docker compose up --build -d

Docker engine not running:
- Start Docker Desktop and wait until it is fully ready

App not updating after edits:
- Rebuild with docker compose up --build -d

This setup is for development and team handoff, not production hardening.

7) First time Committing
    -Create a fork them commit using GitHub Desktop; this is YOUR FORK not editing class repo yet
    -Open your fork: ex: https://github.com/rdupart/DevOpps
    -Click Contribute → Open pull request
    -Base repo: 2026-Spring-A451-Wolfe/DevOpps, -base branch: main
    -Compare repo: rdupart/DevOpps, compare -branch: main
    -Create PR
8. Database Management
    -The folder db is where the final database is
    -in Datagrip db team will create and maintain schemas, write documentation so changes made in datagrip are tracked, validate queries/endpoitns against real data
    -Instructions:
        1. Start Stack:docker compose up --build -d
        2. In datagrip connect it by inputting: Host: localhost
        Port: 55432
        DB/user/pass: from .env
        3. Make chnages to the database via datagrip editor, and once done save sql script chnages in the db folder; ex:db/migrations/001_init.sql
        Tip: having a convention is a good idea for ex: db/migrations, db/seeds, etc.
