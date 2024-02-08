#### 7.1- Get the number of node candidates endpoint:

**Description**: Return the number of available node candidates according to the registered clouds

**Path**:

```url
GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodecandidates/length
```

**Headers:** sessionid

**Returns**: The number of node candidates accorded to the registered clouds

#### 7.2- Add nodes endpoint:

**Description**: Map node candidates with tasks of a defined job. For more details check: [NodeRest class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/rest/NodeRest.java) and [NodeService class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/service/NodeService.java)

**Path**:

```url
POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodes/<JOB_ID>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Body:** A JSON body following this format:

```json
[
    {
        "nodeName": "component-App-1-0",
        "taskName": "Component_App",
        "nodeCandidateId": "<NODE_CANDIDATE_ID>",
        "cloudId": "<CLOUD_ID>"
    },
    {
        "nodeName": "component-LB-1-0",
        "taskName": "Component_LB",
        "nodeCandidateId": "<NODE_CANDIDATE_ID>",
        "cloudId": "<CLOUD_ID>"
    }
]
```

**Returns**: True if the association was successfully established, false otherwise

#### 7.3- Get nodes endpoint:

**Description**: Get all nodes or only those matching with specified ones

**Path**:

```url
GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodes
```

**Headers:** sessionid

**Returns**: A JSON list of the node mapping to the job tasks:

```json
[
    {
        "nodeName": "component-App-1-0",
        "emsDeployment": null,
        "isDeployed": false,
        "nodeAccessToken": null,
        "number": 0,
        "instanceId": null,
        "ipAddress": null,
        "deploymentType": "IAAS",
        "iaasNode": {
            "numDeployments": 1,
            "nodeCandidate": {
                "id": "2c9280838d56156e018d5617d7e70014",
                "nodeCandidateType": "IAAS",
                "jobIdForByon": null,
                "jobIdForEdge": null,
                "price": 0.0472,
                "cloud": {
                    "id": "nebulous-aws-sal-1",
                    "endpoint": null,
                    "cloudType": "PUBLIC",
                    "api": {
                        "providerName": "aws-ec2"
                    },
                    "credential": null,
                    "cloudConfiguration": {
                        "nodeGroup": "",
                        "properties": {}
                    },
                    "owner": null,
                    "state": null,
                    "diagnostic": null
                },
                "location": {
                    "id": "nebulous-aws-sal-1/eu-west-3",
                    "name": "eu-west-3",
                    "providerId": "eu-west-3",
                    "locationScope": "REGION",
                    "isAssignable": true,
                    "geoLocation": {
                        "city": "Paris",
                        "country": "Paris",
                        "latitude": 48.8607,
                        "longitude": 2.3281
                    },
                    "parent": null,
                    "state": null,
                    "owner": null
                },
                "image": {
                    "id": "nebulous-aws-sal-1/eu-west-3/ami-0dcef913833a35715",
                    "name": "PrEstoCloud-Golden-Image-191205-6",
                    "providerId": "ami-0dcef913833a35715",
                    "operatingSystem": {
                        "operatingSystemFamily": "UNKNOWN_OS_FAMILY",
                        "operatingSystemArchitecture": "I386",
                        "operatingSystemVersion": 0.00
                    },
                    "location": {
                        "id": "nebulous-aws-sal-1/eu-west-3",
                        "name": "eu-west-3",
                        "providerId": "eu-west-3",
                        "locationScope": "REGION",
                        "isAssignable": true,
                        "geoLocation": {
                            "city": "Paris",
                            "country": "Paris",
                            "latitude": 48.8607,
                            "longitude": 2.3281
                        },
                        "parent": null,
                        "state": null,
                        "owner": null
                    },
                    "state": null,
                    "owner": null
                },
                "hardware": {
                    "id": "nebulous-aws-sal-1/eu-west-3/t3.medium",
                    "name": "t3.medium",
                    "providerId": "t3.medium",
                    "cores": 2,
                    "ram": 4096,
                    "disk": 8.0,
                    "fpga": 0,
                    "location": {
                        "id": "nebulous-aws-sal-1/eu-west-3",
                        "name": "eu-west-3",
                        "providerId": "eu-west-3",
                        "locationScope": "REGION",
                        "isAssignable": true,
                        "geoLocation": {
                            "city": "Paris",
                            "country": "Paris",
                            "latitude": 48.8607,
                            "longitude": 2.3281
                        },
                        "parent": null,
                        "state": null,
                        "owner": null
                    },
                    "state": null,
                    "owner": null
                },
                "pricePerInvocation": 0.0,
                "memoryPrice": 0.0,
                "nodeId": "2c9280838d56156e018d5617d7ff0015",
                "environment": null
            },
            "id": "2c9280838d56156e018d5617d7ff0015"
        },
        "byonNode": null,
        "edgeNode": null,
        "cloudId": "nebulous-aws-sal-1",
        "taskId": "FCRnewLight3Component_App"
    }
]
```

#### 7.4- Get nodes of job endpoint:

**Description**: Get nodes related to a job

**Path**:

```url
GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodes/job/<JOB_ID>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Returns**: A JSON list of the node mapping to the job tasks

#### 7.5- Remove Nodes endpoint:

**Description**: Remove specified nodes associations

**Path**:

```url
DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodes/remove
```

**Headers:** sessionid

**Body:** A list of node names to remove following this format:

```json
[
    "<NODE_ID>",
    "<NODE_ID>"
]
```

**Returns**: True if nodes were successfully removed, false otherwise

#### 7.6- Remove Nodes of a job endpoint:

**Description**: Remove nodes associations related to a job

**Path**:

```url
DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodes/remove/job/<JOB_ID>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Body:** None

**Returns**: True if nodes were successfully removed, false otherwise
