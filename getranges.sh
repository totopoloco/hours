#!/usr/bin/env bash

# Use curl to call the API and get the ranges
curl -s -X GET -H "Pragma: no-cache" http://localhost:8384/ranges/3000 | python -m json.tool
echo -e "\n"
