param(
    [Parameter(Position = 0)]
    [int]$start,
    [Parameter(Position = 1)]
    [int]$lunchStart,
    [Parameter(Position = 2)]
    [int]$minutesOfLunchBreak = 30
)

$uri = "http://localhost:8384/ranges"

if ($PSBoundParameters.ContainsKey('start') -and $PSBoundParameters.ContainsKey('lunchStart'))
{
    $uri += "WithStartLunchAndMinutesOfLunchBreak/$start/$lunchStart/$minutesOfLunchBreak"
}
else
{
    $uri += "/$minutesOfLunchBreak"
}

$response = Invoke-WebRequest -Uri $uri -Method Get
$json = $response.Content | ConvertFrom-Json
# Display the rangeDetails array in a table format with separate columns for start and end times
$json.rangeDetails | Select-Object @{ Name = 'start'; Expression = { $_.range.start } }, @{ Name = 'end'; Expression = { $_.range.end } }, duration, durationInHours | Format-Table -AutoSize

# Display the remaining data
$json | Select-Object -Property totalHours, totalHoursInHHMM, expectedLunchTimeInHHMM