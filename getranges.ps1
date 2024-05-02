$response = Invoke-WebRequest -Uri http://localhost:8384/ranges/30
$json = $response.Content | ConvertFrom-Json
$json | ConvertTo-Json -Depth 100 | Write-Output