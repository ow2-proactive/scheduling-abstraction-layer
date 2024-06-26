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

cd <DOCKEE_SAL_DIR>

rm -f ./artefacts/scheduling-abstraction-layer-13.1.0-SNAPSHOT.war

cp <SAL_REPO_HOME_DIR>/sal-service/build/libs/scheduling-abstraction-layer-13.1.0-SNAPSHOT.war ./artefacts

docker-compose down

docker build -t activeeon/sal:latest -f ./Dockerfile --no-cache .

docker-compose up


```
> NOTE: Please change <SAL_REPO_HOME_DIR> and <DOCKEE_SAL_DIR> for the correct ones.


Each time the code is modified in <SAL_REPO_HOME_DIR>, you can simple run this script and it will automatically launch new container with the changes included.
