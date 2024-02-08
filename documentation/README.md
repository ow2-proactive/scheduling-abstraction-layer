# Scheduling Abstraction Layer (SAL)

Scheduling Abstraction Layer (SAL) is an abstraction layer developed as part of the EU project Morphemic. SAL aims to enhance the usability of ProActive Scheduler &amp; Resource Manager by providing abstraction and additional features.

## Table of Contents

*   [Introduction](https://openproject.nebulouscloud.eu/projects/nebulous-collaboration-hub/wiki/deployment-manager-sal-1/#introduction)
*   [Installation](https://openproject.nebulouscloud.eu/projects/nebulous-collaboration-hub/wiki/deployment-manager-sal-1/#installation)
*   [Endpoints](https://openproject.nebulouscloud.eu/projects/nebulous-collaboration-hub/wiki/deployment-manager-sal-1/#endpoints)
*   [License](https://openproject.nebulouscloud.eu/projects/nebulous-collaboration-hub/wiki/deployment-manager-sal-1/#license)

## Introduction

SAL is a project developed under the Morphemic project, part of the EU&#39;s Horizon 2020 initiative. It offers an abstraction layer on top of the ProActive Scheduler &amp; Resource Manager, making it easier for users to interact with the scheduler and take advantage of its features. Whether you want to use SAL as a microservice or deploy it as a Docker container, this repository provides the necessary resources to get you started.

## Installation

SAL can be used either as a standalone microservice or as a Docker container. Choose the approach that best suits your requirements.

For running SAL via docker-compose in front of an already-running ProActive server, use the following docker-compose file (adapted from [https://raw.githubusercontent.com/ow2-proactive/docker/master/sal/docker-compose.yaml](https://raw.githubusercontent.com/ow2-proactive/docker/master/sal/docker-compose.yaml) ).

```yaml
# Place login information into a file named `.env`, it should contain the following:
# MYSQL_ROOT_PASSWORD=<a password, freely chosen>
# PWS_URL=<ProActive Server URL>
# PWS_USERNAME=<ProActive user name>
# PWS_PASSWORD=<ProActive user password>
services:
  database:
    image: mariadb
    ports:
      - "3307:3306"
    networks:
      - db-tier
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: proactive
    container_name: myComposeMariaDB
    healthcheck:
      test: [ "CMD", "mariadb-admin" , "ping", "-h", "localhost", "--password=${MYSQL_ROOT_PASSWORD}" ]
      interval: 5s
      timeout: 5s
      retries: 5
  sal:
    image: activeeon/sal:latest
    depends_on:
      database:
        condition: service_healthy
    ports:
      - "8088:8080"
      - "9001:9001"
    links:
      - "database:myComposeMariaDB"
    networks:
      - db-tier
    environment:
      PROPERTIES_FILENAME: sal
      PWS_URL: ${PWS_URL}
      PWS_USERNAME: ${PWS_USERNAME}
      PWS_PASSWORD: ${PWS_PASSWORD}
      DB_USERNAME: root
      DB_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      DB_DRIVER_CLASSNAME: org.mariadb.jdbc.Driver
      DB_URL: jdbc:mariadb://myComposeMariaDB:3306/proactive
      DB_PLATFORM: org.hibernate.dialect.MariaDB53Dialect
      JPDA_ADDRESS: 9001
      JPDA_TRANSPORT: dt_socket
    container_name: myComposeSAL

networks:
  # The presence of these objects is sufficient to define them
  db-tier: {}
```

## Client Library

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

## Endpoints

SAL provides multiple endpoints that you can use to interact with the ProActive Scheduler &amp; Resource Manager:

<figure class="table op-uc-figure_align-center op-uc-figure">
    <table class="op-uc-table">
        <tbody>
            <tr class="op-uc-table--row">
                  <td class="op-uc-p op-uc-table--cell">
                      <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/connection-endpoints.md">Connection endpoints</a>
                  </td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/clouds-endpoints.md">Cloud endpoints</a>
                </td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/byon-endpoints.md">Byon endpoints</a>
                </td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/edge-endpoints.md">Edge endpoints</a>
                </td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/job-endpoints.md">Job endpoints</a>
                </td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/monitoring-endpoints.md">Monitoring endpoints</a>
                </td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/nodes-endpoints.md">Node endpoints</a>
                </td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/nodecandidates-endpoints.md">Node candidates endpoints</a>
                </td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/scaling-endpoints.md">Scaling endpoints</a>
                </td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/documentation/vault-endpoints">Vault endpoints</a>
                </td>
            </tr>
        </tbody>
    </table>
</figure>
