# [gloat](https://vandiedakaf.github.io/)

[![Build Status](https://travis-ci.org/vandiedakaf/gloat.svg?branch=master)](https://travis-ci.org/vandiedakaf/gloat) [![Coverage Status](https://coveralls.io/repos/github/vandiedakaf/gloat/badge.svg)](https://coveralls.io/github/vandiedakaf/gloat)

Time to gloat!

# Local Setup
`./gradlew setupEnv`

#Run
`./gradlew bootRunDev`

# Travis
Required Environment Variables:
* SONAR_TOKEN: *****
* GLOAT_DB_URL: mysql://root:password@localhost:3306/gloat

# Heroku Environment Variables
* SLACK_CLIENT_ID: *****
* SLACK_CLIENT_SECRET: *****
* SLACK_TOKEN: *****
* GLOAT_DB_URL: *****

# ngrok
Use ngrok for local development with slack integration
`ngrok http 8080`