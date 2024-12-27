#### 7.1- findNodeCandidates endpoint:

**Description**:
A node candidate represents a feasible combination of image, location, and hardware. For Cloud providers, SAL analyzes all possible combinations and generates a list of node candidates ready to serve any logic unit. This endpoint provides a mechanism to retrieve and filter these nodes based on `NodeTypeRequirement` and `AttributeRequirement`.

Before using this endpoint, ensure that cloud or edge nodes are added.

*   For the `NodeTypeRequirement` we verify that the node candidate is the type desired. These types are: `IAAS`, `PAAS`, `FAAS`, `BYON`, `EDGE`, `SIMULATION`
*   For the `AttributeRequirement` they are catagorized in the following classes:
    *   `hardware`: In this class we can filter based on the `ram`, `cores`, `disk`, `cpuFrequency`,`fpga`, `gpu`,`name`
    *   `location`: In this class we can filter based on the `geoLocation.country`
    *   `image`: In this class we can filter based on the  `name`, `operatingSystem.family` , `operatingSystem.version`
    *   `cloud`: In this class we can filter based on the `id` and `type`. A cloud type can be `PRIVATE`, `PUBLIC`, `BYON`, `EDGE`
    *   `environment`: In this class we can filter based on the `runtime`. The possible runtimes are `nodejs`, `python`, `java`, `dotnet`, `go`
    *   `name`: In this class we can filter based on the `placementName`. This is used for BYON and EDGE nodes where we can select a specific node to handle a certain component.

For more details about these filters you can check the SAL code [NodeCandidateUtils class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/nc/NodeCandidateUtils.java) To check the Node types: [NodeTypeRequirement class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/NodeTypeRequirement.java) and [NodeType enum](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/NodeType.java) To check the Cloud types: [CloudType enum](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/CloudType.java) to check the requirement operators: [RequirementOperator enum](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/RequirementOperator.java)

**Path**:

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodecandidates
```

**Headers:** `sessionid`

**Body:** A JSON body following this format:

```json
[
  {
    "type": "NodeTypeRequirement",
    "nodeTypes": ["IAAS"]
  },
  {
    "type": "AttributeRequirement",
    "requirementClass": "cloud",
    "requirementAttribute": "id",
    "requirementOperator": "EQ",
    "value": "{{cloud_name}}"
  },
  {
    "type": "AttributeRequirement",
    "requirementClass": "image",
    "requirementAttribute": "operatingSystem.family",
    "requirementOperator": "IN",
    "value": "UBUNTU"
  },
  {
    "type": "AttributeRequirement",
    "requirementClass": "image",
    "requirementAttribute": "name",
    "requirementOperator": "INC",
    "value": "22"
  },
  {
    "type": "AttributeRequirement",
    "requirementClass": "location",
    "requirementAttribute": "name",
    "requirementOperator": "EQ",
    "value": "bgo"
  },
  {
    "type": "AttributeRequirement",
    "requirementClass": "hardware",
    "requirementAttribute": "ram",
    "requirementOperator": "EQ",
    "value": "8192"
  },
  {
    "type": "AttributeRequirement",
    "requirementClass": "hardware",
    "requirementAttribute": "cores",
    "requirementOperator": "EQ",
    "value": "4"
  },
  {
    "type": "AttributeRequirement",
    "requirementClass": "hardware",
    "requirementAttribute": "name",
    "requirementOperator": "EQ",
    "value": "c5.xlarge"
  }
]
```
This JSON requirement is provided as an example of Searching for Node Candidates:
- Node Type: IAAS (cloud node)
- Cloud ID: Matches a specific cloud (use {{cloud_name}} to reference)
- Operating System: Ubuntu, version 22
- Region: bgo
- Hardware Specifications: 8GB RAM and 4 CPU cores
- Hardware Name: c5.xlarge

Please construct your own set of requirements tailored to your specific use case.


**Returns**: A JSON list of Node Candidates.

#### 7.2- getLengthOfNodeCandidates endpoint:

**Description**: This function returns the number of available node candidates according to the added clouds

**Path**:

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodecandidates/length
```

**Headers:** `sessionid`

**Returns**: An integer number of the node candidates.

#### 7.3- addNodes endpoint:

**Description**: associate between node candidate and tasks of a defined job For more details check: [NodeRest class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/rest/NodeRest.java) and [NodeService class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/service/NodeService.java)

**Path**:

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/node/<JOB_ID>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** `sessionid`

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

**Returns**: True if the association was successfully established, false otherwise.

#### 7.4- getNodes endpoint:

**Description**: associate between node candidate and tasks of a defined job

**Path**:

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/node
```

**Headers:** `sessionid`

**Returns**: A JSON list of the node mapping to the job tasks.

#### 7.5- removeNodes endpoint:

**Description**: Remove nodes associations

**Path**:

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/node
```

**Headers:** `sessionid`

**Body:** A JSON body of the node IDs:

```json
[
    "<NODE_ID>",
    "<NODE_ID>"
]
```

**Returns**: True if the node was successfully removed, false otherwise.
