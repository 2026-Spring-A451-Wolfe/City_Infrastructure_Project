# Backend API & Database Containers

The Dockerfile in this folder builds the Tomcat image used by Docker Compose. That container runs the servlet API and serves the web UI from the packaged WAR (`src/main/webapp`). The other Compose service is PostgreSQL.

## API
The Backend API Dockerfile builds from the repository root (`pom.xml` and `src/`) because the WAR must include the whole app. Compose sets `context: .` and `dockerfile: ./BackAPI/Dockerfile`. The container reads the parent `.env` for JDBC and JWT settings. Tomcat listens on port 8080 inside the container; Compose maps it to host port **5050**, so open **http://localhost:5050/** for the UI and API on one origin. The back-api service starts only after the database health check passes.

## DB
The database container is fully instantiated & handled through the docker-compose.yml in the parent directory, where a volume "db_data" is created and maintained to persistently store the database contents, and ./DB/init:/docker-entrypoint-initdb.d is used as a volume mount command to use the files in the DB/init folder of the repository to instantiate and generate all the SQL in those files on the first container use. The database container needs to connect to the port hosting the database, which is currently 5433, through the common PostgreSQL user port, 5432; the docker-compose is currently dynamically setting this up so that, if the host-port is changed, the port used in the command to connect the ports is also automatically changed.