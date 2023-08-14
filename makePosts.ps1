for ($i = 1; $i -le 1000; $i++) {
    $params = @{
        "content" = "content"
        "authorId" = 2
    }

    $headers = @{
        "x-user-id" = "1"
    }

    $json = $params | ConvertTo-Json

    Invoke-WebRequest -Uri "http://localhost:8081/api/post/" -Method POST -Body $json -ContentType "application/json" -Headers $headers
}