param(
    [Parameter()]
    [Alias("s")]
    [int]$start,
    [Parameter()]
    [Alias("l")]
    [int]$lunchStart,
    [Parameter()]
    [Alias("b")]
    [int]$minutesOfLunchBreak
)

$uri = "http://localhost:8384/ranges"
if ($PSBoundParameters.ContainsKey('start') -and $PSBoundParameters.ContainsKey('lunchStart'))
{
    $uri += "With/$start/$lunchStart/$minutesOfLunchBreak"
}
elseif ( $PSBoundParameters.ContainsKey('minutesOfLunchBreak'))
{
    $uri += "/$minutesOfLunchBreak"
}
else
{
    $uri += "/30"
}

Write-Host "$uri"
$response = Invoke-WebRequest -Uri $uri -Method Get
$json = $response.Content | ConvertFrom-Json
# Display the rangeDetails array in a table format with separate columns for start and end times
$json.rangeDetails | Select-Object @{ Name = 'start'; Expression = { (Get-Date $_.range.start).ToString("HH:mm") } }, @{ Name = 'end'; Expression = { (Get-Date $_.range.end).ToString("HH:mm") } }, duration, durationInHours | Format-Table -AutoSize

# Display the remaining data
$json | Select-Object -Property totalHours, totalHoursInHHMM, expectedLunchTimeInHHMM