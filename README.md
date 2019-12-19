# Start
## Gradle (database: H2)
```bash
JWT_SIGNATURES_GENERATOR_SECRET=some_255_bits_long_secret_string
./gradlew run
```

## Docker (database: H2)
```bash
./gradlew jibDockerBuild
docker run -it -p 8080:8080 --env-file .env registry.heroku.com/injury-log/web
```

## Docker Compose (database: Postgresql)
```bash
./gradlew jibDockerBuild
docker-compose up app
```

# Deploy to Heroku (database: Postgresql)
* Remember to set the required environment variables. See .env.example for a complete list
* Depending on the Dyno type, setting the environment variable JAVA_TOOL_OPTIONS to "-Xmx512M -Xms256M" might also be necessary

```bash
docker login --username=_ --password=$(heroku auth:token) registry.heroku.com
./gradlew jib
heroku container:release web
```

# S3 Configuration
S3 is used for file storage and a bucket should be configured. For the sake of user privacy the stored files will be streamed from S3 to this application and then to the client.

Performance wise this is not ideal but making this application act as a gateway should ensure that only files actually owned by the user are served.

The following bucket policy has been applied to the bucket. Granting only a single IAM user access and all public access has been blocked.
```json
{
    "Version": "2012-10-17",
    "Id": "Policy1542998309644",
    "Statement": [
        {
            "Sid": "Stmt1542998308012",
            "Effect": "Allow",
            "Principal": {
                "AWS": "arn:aws:iam::058842494618:user/s3-injury-log"
            },
            "Action": [
                "s3:GetObject",
                "s3:PutObject",
                "s3:DeleteObject"
            ],
            "Resource": [
                "arn:aws:s3:::injury-log/*"
            ]
        }
    ]
}
```

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

# Inspiration
* https://guides.micronaut.io/micronaut-security-jwt-kotlin/guide/index.html
* https://developers.google.com/identity/sign-in/android/backend-auth
* 
