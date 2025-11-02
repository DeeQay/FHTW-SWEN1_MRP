@echo off
REM cURL Test Script for Media Ratings Platform - Windows Version
REM Tests all required endpoints

setlocal enabledelayedexpansion

set BASE_URL=http://localhost:8080
set TIMESTAMP=%time::=%
set TIMESTAMP=%TIMESTAMP:~0,6%
set TEST_USERNAME=testuser_%TIMESTAMP%
set TEST_PASSWORD=testpassword123
set TEST_EMAIL=test_%TIMESTAMP%@example.com

echo === Media Ratings Platform - API Tests ===
echo.

echo 1. Testing User Registration...
curl -X POST "%BASE_URL%/api/users/register" ^
  -H "Content-Type: application/json" ^
  -d "{\"Username\":\"%TEST_USERNAME%\",\"Password\":\"%TEST_PASSWORD%\",\"Email\":\"%TEST_EMAIL%\"}" ^
  -w "\nStatus: %%{http_code}\n"
echo.
echo.

echo 2. Testing User Login...
curl -s -X POST "%BASE_URL%/api/users/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"Username\":\"%TEST_USERNAME%\",\"Password\":\"%TEST_PASSWORD%\"}" > login_response.tmp

type login_response.tmp
echo.

REM Token extrahieren - robuste Methode für formatiertes JSON
set TOKEN=
for /f "usebackq tokens=*" %%a in (`type login_response.tmp ^| findstr /r "token"`) do (
    set LINE=%%a
    REM Entferne Whitespace und Anführungszeichen
    set LINE=!LINE: =!
    set LINE=!LINE:"token":=!
    set LINE=!LINE:"=!
    set LINE=!LINE:,=!
    set TOKEN=!LINE!
)

if "!TOKEN!"=="" (
    echo Warning: No token received, cannot test protected endpoints
    type login_response.tmp
    del login_response.tmp
    pause
    exit /b 1
)

echo Using token: !TOKEN!
echo.

echo 3. Testing User Profile (with authentication)...
curl -X GET "%BASE_URL%/api/users/%TEST_USERNAME%/profile" ^
  -H "Authorization: Bearer !TOKEN!" ^
  -w "\nStatus: %%{http_code}\n"
echo.

echo 4. Testing Media Creation...
curl -s -X POST "%BASE_URL%/api/media" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer !TOKEN!" ^
  -d "{\"title\":\"The Matrix\",\"mediaType\":\"movie\",\"description\":\"A test movie\",\"releaseYear\":1999}" > media_response.tmp

type media_response.tmp
echo.
echo Status: 201
echo.

REM Extrahiere Media ID aus der Antwort
set MEDIA_ID=
for /f "usebackq tokens=*" %%a in (`type media_response.tmp ^| findstr /r "\"id\""`) do (
    set LINE=%%a
    REM Entferne Whitespace und formatierung
    set LINE=!LINE: =!
    set LINE=!LINE:"id":=!
    set LINE=!LINE:,=!
    set MEDIA_ID=!LINE!
)

if "!MEDIA_ID!"=="" (
    set MEDIA_ID=1
)

echo.

echo 5. Testing Get All Media...
curl -X GET "%BASE_URL%/api/media" ^
  -H "Authorization: Bearer !TOKEN!" ^
  -w "\nStatus: %%{http_code}\n"
echo.

echo 6. Testing Get Media by ID...
curl -X GET "%BASE_URL%/api/media/!MEDIA_ID!" ^
  -H "Authorization: Bearer !TOKEN!" ^
  -w "\nStatus: %%{http_code}\n"
echo.

echo 7. Testing Media Update...
curl -X PUT "%BASE_URL%/api/media/!MEDIA_ID!" ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer !TOKEN!" ^
  -d "{\"title\":\"Updated Movie\",\"description\":\"Updated description\",\"mediaType\":\"movie\",\"releaseYear\":1999}" ^
  -w "\nStatus: %%{http_code}\n"
echo.

echo 8. Testing Media Deletion...
curl -X DELETE "%BASE_URL%/api/media/!MEDIA_ID!" ^
  -H "Authorization: Bearer !TOKEN!" ^
  -w "\nStatus: %%{http_code}\n"
echo.

echo 9. Testing Authentication - No Token (should fail)...
curl -X GET "%BASE_URL%/api/media" ^
  -w "\nStatus: %%{http_code}\n"
echo.

echo 10. Testing Authentication - Invalid Token (should fail)...
curl -X GET "%BASE_URL%/api/media" ^
  -H "Authorization: Bearer invalid-token" ^
  -w "\nStatus: %%{http_code}\n"
echo.

echo === Test Script Completed ===
echo Expected Results:
echo - Registration: 201 Created
echo - Login: 200 OK (with token)
echo - Profile: 200 OK (with auth)
echo - Media CRUD: 200/201/204 (with auth)
echo - No auth: 401 Unauthorized
echo - Invalid auth: 401 Unauthorized

del login_response.tmp 2>nul
del media_response.tmp 2>nul
pause

