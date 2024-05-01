#!/usr/bin/env bash

# Use curl to call the API and get the ranges
curl -X GET -H "Pragma: no-cache" http://localhost:8384/ranges/30
echo -e "\n"
