# Start
## Gradle
```bash
JWT_SIGNATURES_GENERATOR_SECRET=some_255_bits_long_secret_string
./gradlew run
```

## Docker
```bash
./gradlew jibDockerBuild
docker run -it -p 8080:8080 --env-file .env registry.heroku.com/injury-log/web
```
Or with docker-compose
```bash
./gradlew jibDockerBuild
docker-compose up
```

# Deploy to Heroku
Remember to set the required environment variables. See .env.example for a complete list.

```bash
docker login --username=_ --password=$(heroku auth:token) registry.heroku.com
./gradlew jib
heroku container:release web
```

# Inspiration
* https://guides.micronaut.io/micronaut-security-jwt-kotlin/guide/index.html
* https://developers.google.com/identity/sign-in/android/backend-auth
* 

# Usage
All of the below examples are for fish shell (https://fishshell.com/) using httpie (https://httpie.org/)

## Set Google Token
```bash
set token "eyJhbGciOiJSUzI1NiIsImtp..."
```

## Set access token
```bash
set access_token (echo "{
            \"username\": \"_\",
            \"password\": \"$token\"
          }" | http :8080/login | jq -r '.access_token')
```

## Create injury
```bash
echo '{
            "description": "My hip hurts... 111122",
            "occurredAt": [2019,11,25,11,22,3,666]
      }' | http post :8080/injuries "Authorization:Bearer $access_token"
```

## Post image
```bash
http --form :8080/injuries/1/images file@original_icon.png "Authorization:Bearer $access_token"
```

## Get image
```bash
http :8080/injuries/1/images/1 "Authorization:Bearer $access_token" > image.png
```

## Delete image
```bash
http delete :8080/injuries/1/images/1 "Authorization:Bearer $access_token"
```
