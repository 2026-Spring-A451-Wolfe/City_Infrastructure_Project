#!/bin/bash

curl -i -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrPhone": "test@example.com",
    "password": "test123"
  }'