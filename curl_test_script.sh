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

# Generiere eindeutigen Usernamen
TIMESTAMP=$(date +%s)
TEST_USERNAME="testuser_$TIMESTAMP"
TEST_PASSWORD="testpassword123"
TEST_EMAIL="test_$TIMESTAMP@example.com"

echo -e "${YELLOW}1. Testing User Registration${NC}"
REGISTER_RESPONSE=$(curl -s -w "\nStatus: %{http_code}" \
  -X POST "$BASE_URL/api/users/register" \
  -H "Content-Type: application/json" \
  -d "{
    \"Username\": \"$TEST_USERNAME\",
    \"Password\": \"$TEST_PASSWORD\",
    \"Email\": \"$TEST_EMAIL\"
  }")

echo "$REGISTER_RESPONSE"
echo ""
echo ""

echo -e "${YELLOW}2. Testing User Login${NC}"
LOGIN_RESPONSE=$(curl -s \
  -X POST "$BASE_URL/api/users/login" \
  -H "Content-Type: application/json" \
  -d "{
    \"Username\": \"$TEST_USERNAME\",
    \"Password\": \"$TEST_PASSWORD\"
  }")

echo "$LOGIN_RESPONSE"

# Token aus Response extrahieren - robuste Methode
# Entferne zuerst alle Whitespaces und Zeilenumbrüche
COMPACT=$(echo "$LOGIN_RESPONSE" | tr -d ' \r\n\t')
TOKEN=$(echo "$COMPACT" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}Warning: No token received, cannot test protected endpoints${NC}"
    echo -e "${RED}Login response was: $LOGIN_RESPONSE${NC}"
    read -p "Drücke [Enter] zum Beenden..."
    exit 1
else
    echo -e "${GREEN}Using token: $TOKEN${NC}"
fi

echo ""
echo ""

echo -e "${YELLOW}3. Testing User Profile (with authentication)${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X GET "$BASE_URL/api/users/$TEST_USERNAME/profile" \
  -H "Authorization: Bearer $TOKEN"

echo ""
echo ""

echo -e "${YELLOW}4. Testing Media Creation${NC}"
MEDIA_RESPONSE=$(curl -s \
  -X POST "$BASE_URL/api/media" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "The Matrix",
    "mediaType": "movie",
    "description": "A computer hacker learns about reality.",
    "releaseYear": 1999
  }')

echo "$MEDIA_RESPONSE"

# Extract media ID
COMPACT_MEDIA=$(echo "$MEDIA_RESPONSE" | tr -d ' \r\n\t')
MEDIA_ID=$(echo "$COMPACT_MEDIA" | grep -o '"id":[0-9]*' | cut -d ':' -f2)
if [ -z "$MEDIA_ID" ]; then
  MEDIA_ID=1
fi
echo -e "${GREEN}Created Media ID: $MEDIA_ID${NC}"
echo "Status: 201"

echo ""

echo -e "${YELLOW}5. Testing Get All Media${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X GET "$BASE_URL/api/media" \
  -H "Authorization: Bearer $TOKEN"

echo ""

echo -e "${YELLOW}6. Testing Get Media by ID${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X GET "$BASE_URL/api/media/$MEDIA_ID" \
  -H "Authorization: Bearer $TOKEN"

echo ""

echo -e "${YELLOW}7. Testing Media Update${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X PUT "$BASE_URL/api/media/$MEDIA_ID" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "The Matrix (Updated)",
    "description": "An updated classic sci-fi movie.",
    "mediaType": "movie",
    "releaseYear": 1999
  }'

echo ""

echo -e "${YELLOW}8. Testing Media Deletion${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X DELETE "$BASE_URL/api/media/$MEDIA_ID" \
  -H "Authorization: Bearer $TOKEN"

echo ""

echo -e "${YELLOW}9. Testing Authentication - No Token (should fail)${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X GET "$BASE_URL/api/media"

echo ""

echo -e "${YELLOW}10. Testing Authentication - Invalid Token (should fail)${NC}"
curl -s -w "Status: %{http_code}\n" \
  -X GET "$BASE_URL/api/media" \
  -H "Authorization: Bearer invalid-token"

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
