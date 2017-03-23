#!/bin/bash

set -e

DOCKER_IMG_PATH=gcr.io/${GCLOUD_PROJECT_ID_STG}/${DOCKER_IMAGE_NAME}

echo "DOCKER BUILD"
docker build -t ${DOCKER_IMG_PATH}:$TRAVIS_COMMIT .

echo $GCLOUD_SERVICE_KEY | base64 --decode > ${HOME}/gcloud-service-key.json
gcloud auth activate-service-account --key-file ${HOME}/gcloud-service-key.json

gcloud --quiet config set project $GCLOUD_PROJECT_ID_STG
gcloud --quiet config set container/cluster $GCLOUD_CLUSTER_NAME_STG
gcloud --quiet config set compute/zone ${CLOUDSDK_COMPUTE_ZONE}
gcloud --quiet container clusters get-credentials $GCLOUD_CLUSTER_NAME_STG

echo "DOCKER PUSH"
gcloud docker push ${DOCKER_IMG_PATH}

yes | gcloud beta container images add-tag ${DOCKER_IMG_PATH}:$TRAVIS_COMMIT ${DOCKER_IMG_PATH}:latest

echo "K8 SETUP"
kubectl config view
kubectl config current-context

echo "K8 RUN"
kubectl run kubernetes-bootcamp --image=${DOCKER_IMG_PATH}:$TRAVIS_COMMIT --port=8080
