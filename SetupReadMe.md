Tomcat + PostgreSQL Docker Demo

This project is a simple multi-container backend setup using:

Java (Servlets)
Apache Tomcat
PostgreSQL
Docker Compose

This project runs PostgreSQL in one container and a Java servlet app on Tomcat in another container.
The app connects to the database using JDBC over the Docker internal network.

SETUP

Requirements

Docker Desktop (Windows or macOS)

This works on:

Windows
macOS

As long as Docker Desktop is installed and running.

Environment Setup
Download the repo from Main, open the folder in VS Code and verify your .env looks like the below code!
.env file:

DB_NAME=city_database  # database name
DB_USER=city_admin     # database username
DB_PASSWORD=city123    # database password
DB_HOST=localhost      # database host
DB_PORT=5433           # database port


Important:

No spaces around =

File must be named exactly .env

File must be in the same folder as docker-compose.yml

If you change the port or your .env file Do NOT commit your personal .env file as the port 5432 is the local Postgres sql install port and will not let you connect!

Note:
Port 5433 is used to avoid conflicts with local PostgreSQL installations.
If you do not have PostgreSQL installed locally, please reach out if you come across issues!

Running the Project

Use Command Prompt (recommended) or terminal.

Navigate to the project directory:

cd DevOpps-main

Then run:

docker compose down -v #removes volume 
docker compose up --build #runs build and clears cache

This will:

Build the Tomcat image

Start PostgreSQL

Start Tomcat

Create the database

Map PostgreSQL to localhost:5433
===================================
To Check Container Status you can run
"docker ps"
or you can open docker desktop and verify its running there!

You should see:

tomcat-demo-db
tomcat-demo-web

Both should be in the “Up” state.

Accessing the Application (optional, mainly used later if only connecting to db you can ignore this)
###############
Open your browser:

http://localhost:8080

If everything is working:

Tomcat deployed correctly

Database connection is active
######################
Connecting to the Database!!!!

You can connect using a DataGrip!

Open Data Grip, click the plus, add data source (postgres) and then fill out the form accordingly!
Use:

Host: localhost
Port: 5433
Database: city_database
User: city_admin
Password: city123

Important:
This connection is from your local machine to Docker until we have CI set up fully, any changes you make in the GUI only WILL NOT SAVE, to upload premade sql files please add the query structure to the Db/init/ folder and code will be updated to run these files as need be!


Do not change that.
--------------------------------
How It’s Structured (if youre curious)

Two containers:

db

PostgreSQL database

Stores relational data

Uses a named Docker volume for persistence

tomcat

Java servlet application

Connects to the database using JDBC

Exposes port 8080

How Requests Flow

Browser
→ Tomcat (Servlet)
→ PostgreSQL

Tomcat connects to the database using the Docker service name “db”, not localhost.

Persistence

The database uses a named Docker volume.

This means:

You can stop containers and your data remains

Rebuilding containers does not delete database rows

To completely reset everything (including database data):

docker compose down -v

Then:

docker compose up -d --build

Team Workflow

Each team member runs their own local Docker environment.

If you create or modify tables:

Add your SQL changes to the project (recommended: create a db/init folder for schema files if not already there)

Commit those SQL changes

Teammates pull updates

Teammates reset their database:

docker compose down -v
docker compose up -d --build

Do not rely on manually editing tables in your GUI as the source of truth.
Schema changes should be committed.

Notes

This setup is development-focused and not production-ready.
Credentials are stored in .env for simplicity.
Ports are exposed for local testing only.
Container startup uses health checks to ensure PostgreSQL is ready before Tomcat starts but optional after first connection. 
