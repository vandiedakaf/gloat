#!/bin/bash

set -e

docker build -t gcr.io/${GCLOUD_PROJECT_ID_STG}/${DOCKER_IMAGE_NAME}:$TRAVIS_COMMIT .

echo $GCLOUD_SERVICE_KEY | base64 --decode > ${HOME}/gcloud-service-key.json
gcloud auth activate-service-account --key-file ${HOME}/gcloud-service-key.json

gcloud --quiet config set project $GCLOUD_PROJECT_ID_STG
gcloud --quiet config set container/cluster $GCLOUD_CLUSTER_NAME_STG
gcloud --quiet config set compute/zone ${CLOUDSDK_COMPUTE_ZONE}
gcloud --quiet container clusters get-credentials $GCLOUD_CLUSTER_NAME_STG

gcloud docker push gcr.io/${GCLOUD_PROJECT_ID_STG}/${DOCKER_IMAGE_NAME}

yes | gcloud beta container images add-tag gcr.io/${GCLOUD_PROJECT_ID_STG}/${DOCKER_IMAGE_NAME}:$TRAVIS_COMMIT gcr.io/${GCLOUD_PROJECT_ID_STG}/${DOCKER_IMAGE_NAME}:latest

kubectl config view
kubectl config current-context

kubectl set image deployment/${KUBE_DEPLOYMENT_NAME} ${KUBE_DEPLOYMENT_CONTAINER_NAME}=gcr.io/${GCLOUD_PROJECT_ID_STG}/${DOCKER_IMAGE_NAME}:$TRAVIS_COMMIT
