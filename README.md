# Scheduling Abstraction Layer (SAL)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 8](https://img.shields.io/badge/Java-8-blue.svg)](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-1.3.8-brightgreen.svg)](https://spring.io/projects/spring-boot)

Scheduling Abstraction Layer (SAL) is an abstraction layer initially developed as part of the EU project Morphemic ([morphemic.cloud](https://morphemic.cloud/)). Its development continued through the Nebulous EU project ([nebulouscloud.eu](https://nebulouscloud.eu/)). SAL aims to enhance the usability of [ProActive Scheduler & Resource Manager](https://proactive.activeeon.com/) by providing abstraction and additional features.

## Table of Contents

- [1. Introduction](#introduction)
- [2. Installation](#installation)
- [3. Usage](#usage)
- [4. Contributing](#contributing)
- [5. License](#license)s

## 1. Introduction

SAL is a project initially developed under the Morphemic project, part of the EU's Horizon 2020 initiative. Its development continued through the Nebulous EU project, part of Horizon Europe inititative. It offers an abstraction layer on top of the ProActive Scheduler & Resource Manager, making it easier for users to interact with the scheduler and take advantage of its features. Seamlessly supporting REST calls and direct communication with the Proactive API, SAL empowers users to harness the scheduler's capabilities. Whether you want to use SAL as a microservice or deploy it as a Docker container, this repository provides the necessary resources to get you started.

## 2. Installation

SAL can be deployed in several ways: as a standalone microservice, within a Docker container, or as a Kubernetes pod. Below are the detailed instructions for each deployment method.

### 2.1. Deploying SAL as a Standalone Microservice
For this deployment approach SAL runs directly on the host system using a Java runtime environment.
It is managed manually, meaning that you control the environment, dependencies, and configurations.
However, it is limited to the capabilities of the host system; scaling requires manual setup of additional instances.
 Relies on the host’s network settings, with manual setup for external access and load balancing.
Suitable for development, small-scale deployments, or when direct control over the runtime environment is needed.

#### 2.1.1. Build and Run the Microservice

To use SAL as a microservice, follow these steps:

1. Clone the SAL repository:

```bash
** pull the SAL project
git clone https://github.com/ow2-proactive/scheduling-abstraction-layer.git

** go to your SAL folder
cd scheduling-abstraction-layer
```

2. Build the microservice:

```bash
** build the project using Gradle
./gradlew spotlessApply clean build --refresh-dependencies -x test
```
The generated `.war` file will be located at: `scheduling-abstraction-layer/sal-service/build/libs/scheduling-abstraction-layer-xxx.war`.

3. Run the Microservice:
```bash
./gradlew bootRun
```
This command starts SAL as microservice on default port `8080` on your host.

#### 2.1.2. Client Library

The `sal-common` Java library provides class definitions for SAL concepts.  It can be added to gradle projects by adding the following into `build.gradle`:

```groovy
repositories {

    maven {
        url 'http://repository.activeeon.com/content/groups/proactive/'
        allowInsecureProtocol = true
    }
}
dependencies {
    // SAL client library
    implementation 'org.ow2.proactive:sal-common:13.1.0-SNAPSHOT'
}
```

### 2.2. Deploying SAL as a Docker Container
In this deployment approach, SAL runs inside a Docker container, providing a consistent environment across different systems. Management is handled via Docker commands or Docker Compose, with containerization isolating the application and its dependencies. While SAL can scale across multiple containers on the same machine, scalability is limited to a single-node setup unless additional tools are utilized. Docker manages networking, though more complex configurations may require manual setup. This method is ideal for consistent deployment across various environments, easier distribution, and meeting basic scalability needs.

SAL can be deployed as a Docker container either by using a pre-built image or by building your own.


#### 2.2.1. Using Pre-Built SAL Docker Images

You can pull the latest or a specific version of the SAL Docker image from remote Docker repository [DockerHub](https://hub.docker.com/r/activeeon/sal/tags):
- `activeeon/sal:dev`: The latest daily release of SAL.
- `activeeon/sal:dev-YYYY-MM-DD`: A specific version of SAL released on a particular date. Replace YYYY-MM-DD with the desired date to pull that specific version.

To pull an image:

```bash
docker pull activeeon/sal:dev
```

#### 2.2.2. Creating a Custom SAL Docker Image:

To create your own Docker image for SAL:

1. Clone the Docker repository:

```bash
git clone https://github.com/ow2-proactive/docker
```

2. Copy the built `.war` file:

Copy the `.war` file generated in section 2.1.1  to the `docker/sal/artefacts` directory.

3. Build the Docker image:

Navigate to the `docker/sal` directory and build the image:
```bash
cd docker/sal
docker build -t activeeon/sal:test -f ./Dockerfile --no-cache .
```

4. Publish the Docker image:

```bash
docker push activeeon/sal:test
```

#### 2.2.3. Run SAL as Docker Container:

**Prerequisites:** Docker installed on your machine.

1. Edit the Docker Compose File:

* Open [docker-compose.yaml](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/docker/docker-compose.yaml)

* Setup connection to the ProActive scheduler

```bash
sal:
      #Set up connection to ProActive server (PWS)
      PWS_URL: <CHANGE_ME>
      PWS_USERNAME: <CHANGE_ME>
      PWS_PASSWORD: <CHANGE_ME>
```

* Setup which SAL image will be used:

```bash
sal:
#Set up image to be used for SAL from https://hub.docker.com/r/activeeon/sal/tags
image: activeeon/sal:test
```
NOTE: It is possible to generate automatically image from `.war` file generated in section 2.1.1. In this case the image tag (e.g. test) should not exist in DockerHub repository.

* Setup SAL ports:
```bash
sal:
    ports:
      - "8088:8080" # sal service ports
      - "9001:9001" # sal-pda service ports for debugging
```

2. Build and Start the Containers:

Open a terminal and navigate to the directory containing your docker-compose.yaml (e.g. docker) file to start docker containers:
```bash
cd .\docker\
docker-compose up --build
```
NOTE: Make sure that previous containers are removed (Step 4)
3. Verify Deployment

Check the status of the containers

```bash
docker-compose ps
```

4. Stop and Remove Containers

```bash
docker-compose down
```

### 2.3. Deploying SAL as a Kubernetes Pod

In this deployment approach, SAL is deployed as a pod within a Kubernetes cluster, which offers advanced orchestration and management features.
Kubernetes automatically handles deployment, scaling, and operations across a cluster of nodes, providing native support for horizontal scaling, automatic load balancing, and self-healing capabilities. The robust networking solutions provided by Kubernetes include service discovery, Ingress controllers, and built-in load balancing. This method is ideal for large-scale, production environments where high availability, scalability, and complex orchestration are required.

To deploy SAL on Kubernetes, it is to use or create a Docker image as described in section 2.2. from remote Docker repository [DockerHub](https://hub.docker.com/r/activeeon/sal/tags). You can then deploy this image as a Kubernetes pod.

**Prerequisites:** Kubernetes cluster (local or cloud-based) and kubectl CLI installed and configured.

1. Edit Kubernetes Deployment and Service Manifests:

Edit [sal.yaml](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/deployment/sal.yaml)
Setup ProActive connection, SAL image and ports as described in 2.2.3. Step 1

NOTE: Update `/path/to/scripts` to the path where your scripts are located on the host machine.

2. Deploy to Kubernetes:

Apply the deployment and service manifests to your Kubernetes cluster:

```bash
kubectl apply -f sal.yaml
```

3. Verify Deployment:

* Check the status of the pods:
```bash
kubectl get pods
```

* Check the status of the services:
```bash
kubectl get services
```

* Access SAL using the service's external IP or via a port-forward:
```bash
kubectl port-forward service/sal-service 8080:8080
```

4. Clean Up:

To delete the deployment and service:
```bash
kubectl delete -f sal.yaml
```

## 3. Usage
Once SAL is deployed, you can interact with it via its REST API, monitor its operation, and view logs to ensure everything is functioning correctly. Here’s how to use SAL effectively.

### 3.1. Accessing SAL REST Endpoints
SAL exposes several REST API endpoints which serves as interfaces that you can use to interact with the ProActive Scheduler & Resource Manager. For detailed information on each endpoint, please go [here](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/README.md).

To showcase usage [Connect](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/1-connection-endpoints.md#11--connect-endpoint) endpoint will use, which should have SAL protocol, host, port and ProActive  username and password set as it was done in deployment `.yaml` files.

Below are the instructions for connecting to and disconnecting from the ProActive server, using tools such as Postman or cURL.

#### 3.1.1 Using Postman

Download and install [Postman](https://www.postman.com/) if you haven’t already.

* Set Up Request: 
  * URL: `http://localhost:8080/sal/pagateway/connect`
  * Method: POST
  * Headers: None
  * Body:
```bash
{
"username": "Proactive server username",
"password": "Proactive server password"
}
```
* Send Request: Click the "Send" button to execute the request and review the response.
* Replay: A text format reply containing the session ID.

#### 3.1.1 Using cURL

* Open Terminal
* Execute cURL Command:
```bash
{
curl -X POST "http://localhost:8080/sal/pagateway/connect" \
  --header "Content-Type: application/json" \
  -d '{"username": "Proactive server username", "password": "Proactive server password"}'

}
```
* Replay: A text format reply containing the session ID.

### 3.2. Checking SAL logs

#### 3.2.1 View Logs for SAL Docker Container
When SAL is deployed as a Docker Container like in section 2.2, you can view its logs using Docker commands.

* Launch your command line interface (CLI).
* List Running Containers: To find the container name or ID, use:
```bash
docker ps
```
Look for the container name `myComposeSAL` or the name you used.
* View SAL general Logs:
```bash
docker logs myComposeSAL
```
* View SAL database general Logs:
```bash
docker logs myComposeMariaDB
```

* View detail SAL Logs inside container:
```bash
docker exec -it myComposeSAL /bin/bash #To enter the SAL container’s shell
cd logs 
cat scheduling-abstraction-layer.log #View detail logs
```
* Query SAL Database i.e. MariaDB:
```bash
docker exec -it myComposeMariaDB /bin/bash
# Replace <password> with the password you've set for the MariaDB root user
mariadb -uroot -p<password> proactive

```


#### 3.2.1 View Logs for SAL Pod
When SAL is deployed as a Kubernetes pod, you can access the logs using `kubectl` commands.
* Get the name of the SAL pod
```bash
kubectl get po -o wide
```
* View SAL general Logs:
```bash
# Replace <namespace> with the appropriate namespace and <pod-name> with the actual pod name obtained in previous step
kubectl -n <namespace> logs <pod-name> sal 
```
* View detail SAL Logs inside container:
```bash
kubectl exec -it <pod-name> -c sal -- /bin/bash #To enter the SAL container’s shell
cd logs 
cat scheduling-abstraction-layer.log
```

* Query SAL Database i.e. MariaDB:
```bash
kubectl exec -it <pod-name> -c mariadb -- mariadb -uroot -p<password> proactive
#Replace <password> with the appropriate MariaDB root password.
```

## 4. Contributing

Contributions to SAL are welcome! If you have any bug fixes, improvements, or new features to propose, please feel free to open a pull request. For major changes, it is recommended to discuss your ideas with the maintainers first.

## 5. License

Scheduling Abstraction Layer (SAL) is distributed under the [MIT License](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/LICENSE). Please see the [LICENSE](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/LICENSE) file for more information.
Note that to use SAL it is necessary to have licence for [ProActive Scheduler & Resource Manager](https://proactive.activeeon.com/).
---

Thank you for using Scheduling Abstraction Layer (SAL)! If you encounter any issues or have questions, please feel free to open an issue in the repository. We hope SAL enhances your experience with ProActive Scheduler & Resource Manager!
