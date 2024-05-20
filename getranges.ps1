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
try {
    $response = Invoke-WebRequest -Uri $uri -Method Get -ErrorAction Stop
}
catch {
    Write-Host "Error details: $($_.Exception.Message)"
    # Try to parse the response content as JSON
    try {
        $json = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($json)
        $json = $reader.ReadToEnd() | ConvertFrom-Json
        if ($json.PSObject.Properties.Name -contains 'extraComments') {
            Write-Host $json.extraComments
        }
    }
    catch {
        Write-Host "Failed to parse server response."
    }
    return
}

$json = $response.Content | ConvertFrom-Json
# Display the rangeDetails array in a table format with separate columns for start and end times
$json.rangeDetails | Select-Object @{ Name = 'start'; Expression = { (Get-Date $_.range.start).ToString("HH:mm") } }, @{ Name = 'end'; Expression = { (Get-Date $_.range.end).ToString("HH:mm") } }, duration, durationInHours | Format-Table -AutoSize
# Display the remaining data
$json | Select-Object -Property totalHours, totalHoursInHHMM, expectedLunchTimeInHHMM