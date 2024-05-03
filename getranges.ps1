param(
    [Parameter(Position = 0)]
    [int]$rangeValue = 30
)

$response = Invoke-WebRequest -Uri http://localhost:8384/ranges/$rangeValue -Method Get
$json = $response.Content | ConvertFrom-Json
# Display the rangeDetails array in a table format with separate columns for start and end times
$json.rangeDetails | Select-Object @{Name='start'; Expression={$_.range.start}}, @{Name='end'; Expression={$_.range.end}}, duration, durationInHours | Format-Table -AutoSize

# Display the remaining data
$json | Select-Object -Property totalHours, totalHoursInHHMM, expectedLunchTimeInHHMM