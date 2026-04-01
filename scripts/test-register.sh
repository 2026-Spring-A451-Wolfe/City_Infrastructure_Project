#!/bin/bash

curl -i -X POST http://localhost:8080/api/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "emailOrPhone": "test@example.com",
    "password": "test123"
  }'