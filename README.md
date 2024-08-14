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

### 3.1. Using SAL REST Endpoints
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

### 3.2. View SAL logs

#### 3.2.1 View Logs for SAL deployed as Docker Container
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


#### 3.2.1 View Logs for SAL deployed as Kubernetes Pod
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
### 3.3. Restarting SAL Service

Restarting the SAL service can be necessary for applying configuration changes, recovering from issues, or for routine maintenance. Note that on the restart SAL logs and database will be erased. Below are the instructions for restarting SAL, both when deployed as a Docker container and as a Kubernetes pod.

#### 3.3.1 Restarting SAL as a Docker Container

If SAL is deployed as a Docker container using `docker-compose.yaml`, you can restart the service using the following methods:

2. Restart a Specific Container:


To restart just the SAL container without affecting other services (like MariaDB):
```bash
docker restart myComposeSAL
```

2. Restart All Services in Docker Compose:
To restart all services defined in your docker-compose.yaml (including the database):

```bash
docker-compose restart
```

3. Rebuild and Restart SAL:
If you need to apply changes to the Docker image or configuration:

```bash
docker-compose up --build -d
```
This command will rebuild the containers if necessary and restart them in detached mode.

#### 3.3.2 Restarting SAL as a Kubernetes Pod

When SAL is deployed as a Kubernetes pod, you can restart the service by following these methods:

1. Rolling Restart (Preferred Method):
Kubernetes allows for a rolling restart, which updates the pods one by one without downtime:

```bash
kubectl rollout restart deployment/sal-deployment
#Replace sal-deployment with the actual name of your SAL deployment.
```

2. Manual Pod Deletion:
Alternatively, you can delete the existing pod(s) manually, and Kubernetes will automatically recreate them:


```bash
kubectl get pods
# Copy sal pod name:
kubectl delete pod <sal-pod-name>
```

Kubernetes will automatically recreate the pod using the existing deployment configuration.

3. Scaling the Deployment to Zero and Back:
Another method to restart SAL in Kubernetes is by scaling the deployment down to zero replicas and then scaling it back up to the desired number of replicas. This effectively stops and restarts the pods:

```bash
kubectl get deployment
# Replace <namespace> with the appropriate namespace (e.g., nebulous-cd) and <sal-deployment-name> with your SAL deployment name.
kubectl scale -n <namespace> --replicas=0 deploy/<sal-deployment-name>
kubectl scale -n <namespace> --replicas=1 deploy/<sal-deployment-name>
```
### 3.4. Checking SAL Deployment
Below are steps for checking a SAL deployment, including checking image versions, container status, and overall health, for both Docker Compose and Kubernetes deployments.

#### 3.4. Checking SAL Deployment in Docker Compose
1. Check Running Containers:

Ensure all services defined in your `docker-compose.yaml` file are running:
```bash
docker-compose ps
```
This command shows the status of each container, including whether they are up and running.

2. Check Image Versions:

Verify that the correct images and versions are used for each service:

```bash
docker inspect <container_name_or_id> | grep Image
# Replace <container_name_or_id> with the container's name or ID (e.g., myComposeSAL, myComposeMariaDB).
```
This will display the images used by each container.


4. Check Health of Containers:

If a health check is defined in the `docker-compose.yaml` (as in the MariaDB service):

```bash
docker inspect --format='{{json .State.Health.Status}}' <container_name_or_id>
```
This will show if the container is healthy, unhealthy, or starting.

#### 3.4.2 Checking SAL Deployment in Kubernetes
For Kubernetes, more advanced checks are available due to the nature of Kubernetes as an orchestrator.

1. Check Running Pods:

List all running pods and their statuses in the relevant namespace:

```bash
kubectl get pods -n <namespace>
# Replace <namespace> with the appropriate namespace
```
This command will show if the pods are running, pending, or in error.

2. Check Deployment Details:

Verify that the correct images are being used in your deployment:

```bash
kubectl get deploy -n <namespace> <deployment_name> -o yaml | grep image
```
This command extracts the image versions specified in the deployment YAML.


3. Check Pod Health and Status:

Check the status and details of the running pods:

```bash
kubectl describe pod -n <namespace> <pod_name>
```
This provides a detailed description of the pod’s state, including events, container statuses, and any errors.

4. Check Service Endpoints:

Ensure that the SAL services are correctly exposed and accessible:

```bash
kubectl get svc -n <namespace>
```

This command lists all services in the namespace, showing their external IPs, ports, and status.

5. Check Resource Utilization:

Monitor resource usage to ensure the deployment is operating within expected parameters:

```bash
kubectl top pods -n <namespace>
```

This shows CPU and memory usage, helping you identify any resource constraints or anomalies.

### 3.5. Debugging SAL

1. Ensure that SAL is running

SAL need to be deployed and prepared for usage as described in section 2.

* **SAL in Docker:**

The `docker-compose.yaml` file includes a debugging service for SAL, exposing port 9001 by default (see section 2.2). This port is typically configured for remote debugging using the Java Debug Wire Protocol (JDWP) so it is sufficient that SAL container is running.

* **SAL as Kubernetes Pod:**

The `sal.yaml` file for Kubernetes also includes configuration for the debugging service, exposing port 9001 by default (see section 2.3). To use debugging service To access the debugging port on your local machine, set up port forwarding from your Kubernetes pod to your local machine:
```bash
kubectl port-forward deployment/sal 9001:9001
#In case the SAL is not deployed as sal, replace it with the actual name of your SAL deployment.
```
Another approach is to use sal-pda service which is deployed by default using `sal.yaml`:
```bash
kubectl get services
#Use the actual name of sal-pda-service and ports with ports as defined in deployment script
kubectl port-forward service/sal-pda-service 9001:9001
```

2. Configure Your IDE for Remote Debugging:

In IntelliJ IDEA:
* Go to Run > Edit Configurations.
* Click the + button and select Remote JVM Debug.
* Set the Host to `localhost` and the Port to `9001` or the one which is used for SAL deployment. In a case there is problem localhost can be replaced with your IP address.
* Set the Debugger mode to Attach to remote JVM.
* Click Apply and then OK

3. Start Debugging:

With your IDE configured, you can now start a debugging session.The following message should show:
```bash
Connected to the target VM, address: 'localhost:9001', transport: 'socket'
```
Use SAL endpoints as described in section 3.1. and set breakpoints in your code. As the SAL service executes, your IDE will stop at these breakpoints, allowing you to inspect variables, step through code, and diagnose issues.
During debugging is advised to check SAL logs as described in section 3.2.

## 4. Contributing

Contributions to SAL are welcome! If you have any bug fixes, improvements, or new features to propose, please feel free to open a pull request. For major changes, it is recommended to discuss your ideas with the maintainers first.

## 5. License

Scheduling Abstraction Layer (SAL) is distributed under the [MIT License](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/LICENSE). Please see the [LICENSE](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/LICENSE) file for more information.
Note that to use SAL it is necessary to have licence for [ProActive Scheduler & Resource Manager](https://proactive.activeeon.com/).
---

Thank you for using Scheduling Abstraction Layer (SAL)! If you encounter any issues or have questions, please feel free to open an issue in the repository. We hope SAL enhances your experience with ProActive Scheduler & Resource Manager!
