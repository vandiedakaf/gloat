# [gloat](https://vandiedakaf.github.io/)

[![Build Status](https://travis-ci.org/vandiedakaf/gloat.svg?branch=master)](https://travis-ci.org/vandiedakaf/gloat) [![Coverage Status](https://coveralls.io/repos/github/vandiedakaf/gloat/badge.svg)](https://coveralls.io/github/vandiedakaf/gloat)

Time to gloat!

# Local Setup
Start a MySQL docker container with `sudo docker run --name mysql -e MYSQL_ROOT_PASSWORD=password -d -p 3306:3306 mysql:5.7.17`.

Access MySQL docker bash via `docker exec -i -t mysql /bin/bash`.

#Run
`gradle bootRun -Dspring.profiles.active=local`

# Travis
Required Environment Variables:
* SPRING_PROFILES_ACTIVE: travis
* SONAR_TOKEN: *****
* SLACK_TOKEN: *****

# environment variables
* GCLOUD_SERVICE_KEY
* GLOAT_DB_PASSWORD: *****
* GLOAT_DB_URL: *****
* GLOAT_DB_USERNAME: *****
* SLACK_CLIENT_ID: *****
* SLACK_CLIENT_SECRET: *****

# ngrok
Use ngrok for local development with slack integration
`ngrok http 8080`

# oauth
https://slack.com/oauth/authorize?client_id=70045358465.121259295136&scope=commands,users:read,chat:write:bot,team:read
