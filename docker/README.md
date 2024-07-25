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

rm -f ./artefacts/scheduling-abstraction-layer-13.1.0-SNAPSHOT.war

cp <SAL_REPO_HOME_DIR>/sal-service/build/libs/scheduling-abstraction-layer-13.1.0-SNAPSHOT.war ./artefacts

docker-compose down

docker build -t activeeon/sal:test -f ./Dockerfile --no-cache .

docker-compose up


```
> NOTE: Please change `<SAL_REPO_HOME_DIR>` and `<DOCKER_SAL_DIR>` to the correct ones. In case SAL is built on Windows, use `./Dockerfile.win` instead of `./Dockerfile`.

Each time the code is modified in `<SAL_REPO_HOME_DIR>`, you can run this script and it will automatically launch a new container with the changes included.
