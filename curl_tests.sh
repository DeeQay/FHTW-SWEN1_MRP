#!/bin/bash
# cURL Test Script for Media Ratings Platform - Intermediate Submission
# Tests all required endpoints according to MRP specifications

BASE_URL="http://localhost:8080"
TOKEN=""

echo "=== Media Ratings Platform - Intermediate API Tests ==="
echo

# Test 1: User Registration
echo "1. Testing User Registration..."
curl -X POST $BASE_URL/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"Username":"testuser","Password":"test123","Email":"test@example.com"}' \
  -w "\nStatus: %{http_code}\n\n"

# Test 2: User Login (must return token)
echo "2. Testing User Login..."
RESPONSE=$(curl -s -X POST $BASE_URL/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"Username":"mustermann","Password":"max"}')
echo "Response: $RESPONSE"

# Extract token (robust: whitespace entfernen, dann parsen)
COMPACT=$(echo "$RESPONSE" | tr -d ' \r\n\t')
TOKEN=$(echo "$COMPACT" | grep -o '"token":"[^"]*"' | cut -d '"' -f4)
echo "Extracted Token: $TOKEN"
echo

# Abbruch, falls kein Token
if [ -z "$TOKEN" ]; then
  echo "No token extracted, aborting protected endpoint tests."
  echo "=== Tests completed ==="
  read -p "[Enter] zum Beenden..."
  exit 1
fi

# Test 3: User Profile (requires authentication)
echo "3. Testing User Profile..."
curl -X GET $BASE_URL/api/users/mustermann/profile \
  -H "Authentication: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

# Test 4: Create Media (requires authentication)
echo "4. Testing Create Media..."
curl -X POST $BASE_URL/api/media \
  -H "Content-Type: application/json" \
  -H "Authentication: Bearer $TOKEN" \
  -d '{"title":"Test Movie","description":"A test movie","mediaType":"movie","releaseYear":2023}' \
  -w "\nStatus: %{http_code}\n\n"

# Test 5: Get All Media (requires authentication)
echo "5. Testing Get All Media..."
curl -X GET $BASE_URL/api/media \
  -H "Authentication: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

# Test 6: Get Media by ID (requires authentication)
echo "6. Testing Get Media by ID..."
curl -X GET $BASE_URL/api/media/1 \
  -H "Authentication: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

# Test 7: Update Media (requires authentication)
echo "7. Testing Update Media..."
curl -X PUT $BASE_URL/api/media/1 \
  -H "Content-Type: application/json" \
  -H "Authentication: Bearer $TOKEN" \
  -d '{"title":"Updated Movie","description":"Updated description"}' \
  -w "\nStatus: %{http_code}\n\n"

# Test 8: Delete Media (requires authentication)
echo "8. Testing Delete Media..."
curl -X DELETE $BASE_URL/api/media/1 \
  -H "Authentication: Bearer $TOKEN" \
  -w "\nStatus: %{http_code}\n\n"

# Test 9: Unauthorized Access (no token)
echo "9. Testing Unauthorized Access..."
curl -X GET $BASE_URL/api/media \
  -w "\nStatus: %{http_code}\n\n"

echo "=== Tests completed ==="

# Skript-Ende: Konsole offen halten
read -p "[Enter] zum Beenden..."
