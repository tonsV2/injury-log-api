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
```bash
docker login --username=_ --password=$(heroku auth:token) registry.heroku.com
./gradlew jib
heroku container:release web
```

# Login
## Get token (bash)
```bash
token=$(echo '{
      "username": "sherlock",
      "password": "password"
    }' | http post :8080/login | jq -r '.access_token')
```

## Get token (fish)
```bash
set token (echo '{
      "username": "sherlock",
      "password": "password"
    }' | http post :8080/login | jq -r '.access_token')
```

## Access secure endpoint
```bash
http :8080/home "Authorization:Bearer $token"
```
