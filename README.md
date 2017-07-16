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
* SONAR_TOKEN: *****

# Heroku Environment Variables
* SLACK_CLIENT_ID: *****
* SLACK_CLIENT_SECRET: *****
* SLACK_TOKEN: *****

# ngrok
Use ngrok for local development with slack integration
`ngrok http 8080`