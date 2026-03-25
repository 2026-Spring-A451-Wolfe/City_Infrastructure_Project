# NOLA Infrastructure Reporting \& Tracking System

# Developer Command Reference

# Last Updated: 03/23/2026

\----------------------------------------------------------------

\-------------------------------------------------------------

## Docker

### Run (with fresh build)

```bash
docker compose up --build
```

### Run (force clean rebuild — no cache)

```bash
docker compose build --no-cache
docker compose up
```

### Stop and Remove Existing Containers + Volumes

```bash
docker compose down -v
```

### Check Logs

```bash
docker logs <container\_name>
# Example:
docker logs tomcat
```

### Stream Logs in Real Time

```bash
docker logs -f <container\_name>
```

### Verify Environment Variables Inside Container

```bash
docker exec -it <container\_name> env | grep JWT\_SECRET
```

\-------------------------------------------------------------

\-------------------------------------------------------------

## Auth Endpoints

### Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \\
  -H "Content-Type: application/json" \\
  -d '{
    "username": "your\_username",
    "email\_Or\_Phone": "your\_email\_or\_phone",
    "password": "your\_password"
  }'
```

### Log In (returns a JWT token)

```bash
curl -X POST http://localhost:8080/api/auth/login \\
  -H "Content-Type: application/json" \\
  -d '{
    "email\_Or\_Phone": "your\_email\_or\_phone",
    "password": "your\_password"
  }'
```

### Check Who You Are (verify token)

```bash
curl http://localhost:8080/api/auth/me \\
  -H "Authorization: Bearer <token>"
```

\-------------------------------------------------------------

\-------------------------------------------------------------

## Report Endpoints

### Get All Reports (public, no token needed)

```bash
curl http://localhost:8080/api/reports
```

### Get Single Report (public, no token needed)

```bash
curl http://localhost:8080/api/reports/<id>
```

### Get Updates for a Report (public, no token needed)

```bash
curl http://localhost:8080/api/reports/<id>/updates
```

### Get Current User's Reports (token required — Citizen or Admin)

```bash
curl http://localhost:8080/api/reports/my \\
  -H "Authorization: Bearer <token>"
```

### Create a Report (token required — Citizen or Admin)

```bash
curl -X POST http://localhost:8080/api/reports \\
  -H "Authorization: Bearer <token>" \\
  -H "Content-Type: application/json" \\
  -d '{
    "title": "Report Title",
    "description": "Description of the issue",
    "category": "Pothole",
    "severity": "High",
    "latitude": 29.9511,
    "longitude": -90.0715
  }'
```

Valid category values: Pothole, Flooding, Streetlight, Sign\_Damage, Road\_Damage, Debris, Other
Valid severity values: Low, Medium, High, Critical

### Update Report Status (token required — Admin only)

```bash
curl -X PUT http://localhost:8080/api/reports/<id>/status \\
  -H "Authorization: Bearer <token>" \\
  -H "Content-Type: application/json" \\
  -d '{
    "newStatus": "In\_Progress",
    "comment": "Crew has been dispatched"
  }'
```

Valid status values: Requested, Open, In\_Progress, Resolved, Closed, Rejected

### Delete a Report (token required — Admin only)

```bash
curl -X DELETE http://localhost:8080/api/reports/<id> \\
  -H "Authorization: Bearer <token>"
```

⚠️ Deleting a report also deletes all its updates and images (cascade).

\-------------------------------------------------------------

\-------------------------------------------------------------

## Image Endpoints

### Upload an Image to a Report (token required — Citizen or Admin)

```bash
curl -X POST http://localhost:8080/api/images/<reportId> \\
  -H "Authorization: Bearer <token>" \\
  -F "image=@/path/to/your/photo.jpg"
```

⚠️ URL changed from /api/reports/{id}/images → /api/images/{id}
Accepted file types: JPEG, PNG, WEBP — max 5MB

### Get All Images for a Report (public, no token needed)

```bash
curl http://localhost:8080/api/images/report/<reportId>
```

\-------------------------------------------------------------

\-------------------------------------------------------------

## Department Endpoints (all public, no token needed)

### Get All Departments

```bash
curl http://localhost:8080/api/departments
```

### Get Single Department

```bash
curl http://localhost:8080/api/departments/<id>
```

### Get Department Contacts

```bash
curl http://localhost:8080/api/departments/<id>/contacts
```

\-------------------------------------------------------------

\-------------------------------------------------------------

## Windows curl Notes

* Windows Command Prompt does NOT support single quotes in curl
* Use double quotes and escape inner quotes with "
* Example:

```bash
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\\"email\_or\_phone\\": \\"your\_email\\", \\"password\\": \\"your\_password\\"}"
```

