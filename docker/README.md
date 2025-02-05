# Description

This Docker compose contains two containers:
- The container containing SAL as a service.
- Maria DB container.

# Docker compose Automation

To automate the deployment of the docker compose of SAL you create the following script:

```bash
#!/bin/bash

cd <SAL_REPO_HOME_DIR>

./gradlew spotlessApply clean build --refresh-dependencies

cd <DOCKER_SAL_DIR>

docker-compose down

docker-compose up --build


```
> NOTE: Please change `<SAL_REPO_HOME_DIR>` and `<DOCKER_SAL_DIR>` to the correct ones. In case SAL is built on Windows, use `./Dockerfile.win` instead of `./Dockerfile`.

Each time the code is modified in `<SAL_REPO_HOME_DIR>`, you can run this script and it will automatically launch a new container with the changes included.

# Kubernetes deployment

Example of deployment script for kubernetes without persistent volume can be found under ./kubernetes/sal.yaml

The example of deployment with persistent volume can be found at: https://github.com/eu-nebulous/helm-charts/tree/main/charts/nebulous-sal
