#!/usr/bin/env bash

# Default values
start=""
lunch=""
minutesOfLunchBreak="30"

# Help function
usage() {
  echo "Usage: $0 [-s start] [-l lunch] [-b minutesOfLunchBreak]"
  echo "  -s: Start time"
  echo "  -l: Lunch time"
  echo "  -b: Minutes of lunch break (default: 30)"
  exit 1
}

# Parse command-line options
while getopts "hs:l:b:" opt; do
  case ${opt} in
    h)
      usage
      ;;
    s)
      start=$OPTARG
      ;;
    l)
      lunch=$OPTARG
      ;;
    b)
      minutesOfLunchBreak=$OPTARG
      ;;
    \?)
      echo "Invalid option: -$OPTARG" 1>&2
      usage
      ;;
    :)
      echo "Option -$OPTARG requires an argument." 1>&2
      usage
      ;;
  esac
done
shift $((OPTIND -1))

uri="http://localhost:8384/ranges"

if [ -n "$start" ] && [ -n "$lunch" ]; then
    uri+="WithStartLunchAndMinutesOfLunchBreak/$start/$lunch/$minutesOfLunchBreak"
elif [ -n "$minutesOfLunchBreak" ]; then
    uri+="/$minutesOfLunchBreak"
fi

#Print uri
echo $uri
echo "---------------------------------------------"
response=$(curl -s $uri)
json=$(echo $response | jq '.')

#Print column headers
echo "Start    End      Duration Duration in Hours"

# Display the rangeDetails array in a table format with separate columns for start and end times
echo $json | jq -r '.rangeDetails[] | "\(.range.start) \(.range.end) \(.duration)    \(.durationInHours)"'

echo "---------------------------------------------"
# Display the remaining data with labels
echo "Total Hours:                   $(echo $json | jq -r '.totalHours')"
echo "Total Hours in HH:MM:          $(echo $json | jq -r '.totalHoursInHHMM')"
echo "Expected Lunch Time in HH:MM:  $(echo $json | jq -r '.expectedLunchTimeInHHMM')"