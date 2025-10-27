#!/bin/bash

# cURL Test Script für Media Ratings Platform (Intermediate)
# Testet alle API-Endpunkte der Intermediate-Submission

echo "=== Media Ratings Platform - API Tests ==="
echo ""

BASE_URL="http://localhost:8080"

# Farben für Output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}1. Testing User Registration${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X POST "$BASE_URL/api/users/register" \
  -H "Content-Type: application/json" \
  -d '{
    "Username": "testuser",
    "Password": "testpassword123",
    "Email": "test@example.com"
  }'

echo ""
echo ""

echo -e "${YELLOW}2. Testing User Login (Beispiel aus PDF)${NC}"
LOGIN_RESPONSE=$(curl -s \
  -X POST "$BASE_URL/api/users/login" \
  -H "Content-Type: application/json" \
  -d '{
    "Username": "mustermann",
    "Password": "max"
  }')

echo "$LOGIN_RESPONSE"

# Token aus Response extrahieren (robust: Whitespace entfernen, dann parsen)
COMPACT=$(echo "$LOGIN_RESPONSE" | tr -d ' \r\n\t')
TOKEN=$(echo "$COMPACT" | grep -o '"token":"[^"]*"' | cut -d '"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}Warning: No token received, cannot test protected endpoints${NC}"
else
    echo -e "${GREEN}Using token: $TOKEN${NC}"
fi

echo ""
echo ""

echo -e "${YELLOW}3. Testing User Profile (with authentication)${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X GET "$BASE_URL/api/users/mustermann/profile" \
  -H "Authentication: Bearer $TOKEN"

echo ""
echo ""

echo -e "${YELLOW}4. Testing Media Creation${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X POST "$BASE_URL/api/media" \
  -H "Content-Type: application/json" \
  -H "Authentication: Bearer $TOKEN" \
  -d '{
    "title": "The Matrix",
    "mediaType": "movie",
    "description": "A computer hacker learns about reality.",
    "releaseYear": 1999
  }'

echo ""

echo -e "${YELLOW}5. Testing Get All Media${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X GET "$BASE_URL/api/media" \
  -H "Authentication: Bearer $TOKEN"

echo ""

echo -e "${YELLOW}6. Testing Get Media by ID${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X GET "$BASE_URL/api/media/1" \
  -H "Authentication: Bearer $TOKEN"

echo ""

echo -e "${YELLOW}7. Testing Media Update${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X PUT "$BASE_URL/api/media/1" \
  -H "Content-Type: application/json" \
  -H "Authentication: Bearer $TOKEN" \
  -d '{
    "title": "The Matrix (Updated)",
    "description": "An updated classic sci-fi movie.",
    "mediaType": "movie",
    "releaseYear": 1999
  }'

echo ""

echo -e "${YELLOW}8. Testing Media Deletion${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X DELETE "$BASE_URL/api/media/1" \
  -H "Authentication: Bearer $TOKEN"

echo ""

echo -e "${YELLOW}9. Testing Authentication - No Token (should fail)${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X GET "$BASE_URL/api/media"

echo ""

echo -e "${YELLOW}10. Testing Authentication - Invalid Token (should fail)${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X GET "$BASE_URL/api/media" \
  -H "Authentication: Bearer invalid-token"

echo ""

echo -e "${GREEN}=== Test Script Completed ===${NC}"
echo -e "${YELLOW}Expected Results:${NC}"
echo "- Registration: 201 Created"
echo "- Login: 200 OK (with token)"
echo "- Profile: 200 OK (with auth)"
echo "- Media CRUD: 200/201/204 (with auth)"
echo "- No auth: 401 Unauthorized"
echo "- Invalid auth: 401 Unauthorized"

# Skript-Ende: Konsole offen halten
read -p "Drücke [Enter] zum Beenden..."
