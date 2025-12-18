#! /bin/bash

TOKEN="user:token"
CONTROLLER_URL=https://ci.example.com
curl -XPOST --data-urlencode  "script=$(cat ./jobs-printJobsHangOnInputStep.groovy)" -L -s --user $TOKEN $CONTROLLER_URL/scriptText