#!/bin/bash
# Exit immediately if a command exits with a non-zero status.
set -e

echo "Running deploy-staging script..."

if [[ -z "${GLOAT_DB_PASSWORD}" ]]; then
  echo "GLOAT_DB_PASSWORD does not exist"
  exit 1
fi
if [[ -z "${GLOAT_DB_URL}" ]]; then
  echo "GLOAT_DB_URL does not exist"
  exit 1
fi
if [[ -z "${GLOAT_DB_USERNAME}" ]]; then
  echo "GLOAT_DB_USERNAME does not exist"
  exit 1
fi

sed -i -e "s@|GLOAT_DB_PASSWORD@$GLOAT_DB_PASSWORD@" src/main/appengine/app.yaml
sed -i -e "s@|GLOAT_DB_URL@$GLOAT_DB_URL@" src/main/appengine/app.yaml
sed -i -e "s@|GLOAT_DB_USERNAME@$GLOAT_DB_USERNAME@" src/main/appengine/app.yaml
sed -i -e "s@|SLACK_CLIENT_ID@$SLACK_CLIENT_ID@" src/main/appengine/app.yaml
sed -i -e "s@|SLACK_CLIENT_SECRET@$SLACK_CLIENT_SECRET@" src/main/appengine/app.yaml
sed -i -e "s@|SLACK_TOKEN@$SLACK_TOKEN@" src/main/appengine/app.yaml

echo $GCLOUD_SERVICE_KEY | base64 --decode > ${HOME}/gcloud-service-key.json
gcloud auth activate-service-account --key-file ${HOME}/gcloud-service-key.json

gcloud --quiet config set project $GCLOUD_PROJECT_ID_STG
gcloud --quiet config set compute/zone $CLOUDSDK_COMPUTE_ZONE

echo "*************************************"
cat src/main/appengine/app.yaml

./gradlew appengineDeploy --info

