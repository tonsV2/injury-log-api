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
