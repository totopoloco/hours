$response = Invoke-WebRequest -Uri http://localhost:8384/ranges/30
$json = $response.Content | ConvertFrom-Json
# Display the rangeDetails array in a table format
$json.rangeDetails | Format-Table

# Display the remaining data
$json | Select-Object -Property totalHours, totalHoursInHHMM, expectedLunchTimeInHHMM