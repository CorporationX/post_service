for ($i = 1; $i -le 1000; $i++) {
    $params = @{
  	"entityType" = "POST"
  	"taskType" = "DELETING_POST"
  	"entityId" = $i
  	"scheduleAt" = "2023-08-11T13:50:00"
    }

    Invoke-WebRequest -Uri http://localhost:8081/scheduled/ -Method POST -Body ($params|ConvertTo-Json) -ContentType "application/json"
}