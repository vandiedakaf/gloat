# [gloat](https://vandiedakaf.github.io/)

[![Build Status](https://travis-ci.org/vandiedakaf/gloat.svg?branch=master)](https://travis-ci.org/vandiedakaf/gloat) [![Coverage Status](https://coveralls.io/repos/github/vandiedakaf/gloat/badge.svg)](https://coveralls.io/github/vandiedakaf/gloat)

Time to gloat!

#Run
gradle bootRun -Dspring.profiles.active=local

# Travis
Required Environment Variables:
* SPRING_PROFILES_ACTIVE: travis
* SONAR_TOKEN: *****
* SLACK_TOKEN: *****

# environment variables
* GCLOUD_SERVICE_KEY
* SLACK_CLIENT_ID: *****
* SLACK_CLIENT_SECRET: *****
* SLACK_TOKEN: *****

# ngrok
ngrok http 8080

# oauth
https://slack.com/oauth/authorize?client_id=70045358465.121259295136&scope=commands,users:read,chat:write:bot,team:read
