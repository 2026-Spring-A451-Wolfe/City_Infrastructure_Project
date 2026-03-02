Tomcat + PostgreSQL Docker Demo



This project is a simple multi-container backend setup using:



Java (Servlets)



Apache Tomcat



PostgreSQL



pgAdmin (not really)



Docker Compose



My goal was to build a demo of how the tech stack might work together, definitely a work in progress...



This Project runs PostgreSQL in its own container as well as a Java servlet app on Tomcat in another container

that connects the app to the database using JDBC




SETTTT UPPPPPPP

Requirements



Docker Desktop (Windows or macOS)



This works on:



Windows



macOS



As long as Docker Desktop is installed and running.



Environment Setup



\###Hopefully we can set this up for everyone but i will still commit my personal .env file in case anyone needs help or example 2/11/2026 -Ethan

Create a .env file in the root directory:



DB_HOST=db
DB_PORT=5432
DB_NAME=citydb
DB_USER=city
DB_PASSWORD=citypass



Important:

\*\*

No spaces around =

\*\*

File must be named exactly .env

\*\*

File must be in the same folder as docker-compose.yml



-----------------------

Running the Project: use cmd or git (use cmd line pls and use "cd" command to get to 'tomcat-demo' directory



Once there you can run:



docker compose down -v

docker compose up --build



\###

To check container status:



docker compose ps

(everything should work only run if docker desktop does not show instance)



You should see three services running:



db



tomcat



All in the “Up” state.





How It’s Structured



Three containers:



db



PostgreSQL database



Stores relational data



Uses a named Docker volume for persistence



tomcat



Java servlet application



Connects to the database using JDBC



Exposes port 8080





Web-based database admin interface



Connects to PostgreSQL internally



Exposes port 5050



How Requests Flow



Browser

→ Tomcat (Servlet)

→ PostgreSQL





Database Connection



The application connects using:



jdbc:postgresql://db:5432/demo





db is the Docker service name.



Credentials are injected using environment variables instead of being hardcoded in the source code.



Persistence



The database uses a named Docker volume.



This means:



You can stop containers and your data remains.



Rebuilding containers does not delete database rows.





**To completely reset everything:**



**docker compose down -v**



Health-based startup dependency management

(self checks that database is fine prior to launch)



Notes



This setup is development-focused, not production-ready.



Credentials are stored in .env for simplicity.

(should be non sharable in final version)



Ports are exposed for local testing im aware that the generic ports have no security, we can find a workaround <3!


**Ignore for now!**



Accessing the Services

Web Application

http://localhost:8080

\###



This confirms:



Tomcat deployed correctly


Db Connection is maintained!




