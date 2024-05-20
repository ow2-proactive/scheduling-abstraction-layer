# Scheduling Abstraction Layer (SAL)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 8](https://img.shields.io/badge/Java-8-blue.svg)](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-1.3.8-brightgreen.svg)](https://spring.io/projects/spring-boot)

Scheduling Abstraction Layer (SAL) is an abstraction layer initially developed as part of the EU project Morphemic ([morphemic.cloud](https://morphemic.cloud/)). Its development continued through the Nebulous EU project ([nebulouscloud.eu](https://nebulouscloud.eu/)). SAL aims to enhance the usability of [ProActive Scheduler & Resource Manager](https://proactive.activeeon.com/) by providing abstraction and additional features.

## Table of Contents

- [Introduction](#introduction)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Endpoints](#endpoints)
- [Contributing](#contributing)
- [License](#license)

## Introduction

SAL is a project initially developed under the Morphemic project, part of the EU's Horizon 2020 initiative. Its development continued through the Nebulous EU project, part of Horizon Europe inititative. It offers an abstraction layer on top of the ProActive Scheduler & Resource Manager, making it easier for users to interact with the scheduler and take advantage of its features. Seamlessly supporting REST calls and direct communication with the Proactive API, SAL empowers users to harness the scheduler's capabilities. Whether you want to use SAL as a microservice or deploy it as a Docker container, this repository provides the necessary resources to get you started.

## Installation

SAL can be used either as a standalone microservice or as a Docker container. Choose the approach that best suits your requirements.

### As Microservice

To use SAL as a microservice, follow these steps:

1. Clone the repository:

```bash
git clone https://github.com/ow2-proactive/scheduling-abstraction-layer.git
cd scheduling-abstraction-layer
```

2. Build the microservice:

```bash
./gradlew clean build
```

### As Docker Container

To use SAL as a Docker container, pull the public Docker image from DockerHub:

```bash
docker pull activeeon/sal
```

## Usage

### Using SAL as a Microservice

To run SAL as a microservice, execute the following command:

```bash
./gradlew bootRun
```

This will start the microservice allowing you to interact with it through various endpoints.

### Using SAL as a Docker Container

To deploy SAL as a Docker container, run the following command:

```bash
docker run -p 8080:8080 activeeon/sal
```

This will start the SAL service within a Docker container, and it will be accessible on port 8080.

## Configuration

Before using SAL, you need to configure the ProActive Server it will connect to. Use the following endpoints for configuration:

- To initialize the ProActive Server, use the init endpoint:

```
{protocol}://{host}:{port}/sal/pagateway/init
```

- To connect to the ProActive Server, use the connect endpoint:

```
{protocol}://{host}:{port}/sal/pagateway/connect
```

## Endpoints

[//]: #TODO (javadoc link to be added)
SAL provides multiple endpoints that you can use to interact with the ProActive Scheduler & Resource Manager. For detailed information on each endpoint, please refer to the project's [Javadoc](https://link-to-javadoc).

## Contributing

Contributions to SAL are welcome! If you have any bug fixes, improvements, or new features to propose, please feel free to open a pull request. For major changes, it is recommended to discuss your ideas with the maintainers first.

## License

Scheduling Abstraction Layer (SAL) is distributed under the [MIT License](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/LICENSE). Please see the [LICENSE](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/LICENSE) file for more information.

---

Thank you for using Scheduling Abstraction Layer (SAL)! If you encounter any issues or have questions, please feel free to open an issue in the repository. We hope SAL enhances your experience with ProActive Scheduler & Resource Manager!