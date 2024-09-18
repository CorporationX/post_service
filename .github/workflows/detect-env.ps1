$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$envFilePath = Join-Path $scriptDir "env.txt"

$os = Get-ComputerInfo | Select-Object -ExpandProperty OsName

if ($os -like "*Windows*") {
    echo "ENVIRONMENT=windows-latest" | Out-File -FilePath $envFilePath -Encoding utf8 -Append
    $ENVIRONMENT = "windows-latest"
} elseif ($os -like "*Linux*") {
    echo "ENVIRONMENT=ubuntu-latest" | Out-File -FilePath $envFilePath -Encoding utf8 -Append
    $ENVIRONMENT = "ubuntu-latest"
} elseif ($os -like "*Mac*") {
    echo "ENVIRONMENT=macos-latest" | Out-File -FilePath $envFilePath -Encoding utf8 -Append
    $ENVIRONMENT = "macos-latest"
} else {
    echo "ENVIRONMENT=ubuntu-latest" | Out-File -FilePath $envFilePath -Encoding utf8 -Append
    $ENVIRONMENT = "ubuntu-latest"
}

echo $ENVIRONMENT