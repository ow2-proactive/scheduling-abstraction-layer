#### 7.1- findNodeCandidates endpoint:

**Description**: A node candidate is a combination of an image, location, and hardware that are feasiable together. In the case of a Cloud provider SAL will analyze all these possible combinations and create a list of node candidates that are ready to be servered to any logic unit. In this endpoint we define a way to retrieve and filter these nodes using `NodeTypeRequirement` and `AttributeRequirement`.

*   For the `NodeTypeRequirement` we verify that the node candidate is the type desired. These types are: `IAAS`, `PAAS`, `FAAS`, `BYON`, `EDGE`, `SIMULATION`
*   For the `AttributeRequirement` they are catagorized in the following classes:
    *   `hardware`: In this class we can filter based on the `ram`, `cores`, `disk`, `fpga`.
    *   `location`: In this class we can filter based on the `geoLocation.country`
    *   `image`: In this class we can filter based on the `name`, `operatingSystem.family` , `operatingSystem.version`
    *   `cloud`: In this class we can filter based on the `type`. A cloud type can be `PRIVATE`, `PUBLIC`, `BYON`, `EDGE`
    *   `environment`: In this class we can filter based on the `runtime`. The possible runtimes are `nodejs`, `python`, `java`, `dotnet`, `go`
    *   `name`: In this class we can filter based on the `placementName`. This is used for BYON and EDGE nodes where we can select a specific node to handle a certain component.

For more details about these filters you can check the SAL code [NodeCandidateUtils class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/nc/NodeCandidateUtils.java) To check the Node types: [NodeTypeRequirement class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/NodeTypeRequirement.java) and [NodeType enum](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/NodeType.java) To check the Cloud types: [CloudType enum](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/CloudType.java) to check the requirment operators: [RequirementOperator enum](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/RequirementOperator.java)

**Path**:

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodecandidates
```

**Headers:** sessionid

**Body:** A JSON body following this format:

```json
[
    {
        "type": "NodeTypeRequirement",
        "nodeTypes": ["IAAS"]
    },
    {
        "type": "AttributeRequirement",
        "requirementClass": "hardware",
        "requirementAttribute": "cores",
        "requirementOperator": "EQ",
        "value": "2" 
    },
        {
        "type": "AttributeRequirement",
        "requirementClass": "hardware",
        "requirementAttribute": "ram",
        "requirementOperator": "EQ",
        "value": "4096" \\ in mb
    }
]
```

**Returns**: A JSON list of Node Candidates.

#### 7.2- getLengthOfNodeCandidates endpoint:

**Description**: This function returns the number of available node candidates according to the added clouds

**Path**:

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodecandidates
```

**Headers:** sessionid

**Returns**: An integer number of the node candidates.

#### 7.3- addNodes endpoint:

**Description**: associate between node candidate and tasks of a defined job For more details check: [NodeRest class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/rest/NodeRest.java) and [NodeService class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/service/NodeService.java)

**Path**:

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/node/<JOB_ID>
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

**Returns**: True if the association was successfully established, false otherwise.

#### 7.4- getNodes endpoint:

**Description**: associate between node candidate and tasks of a defined job

**Path**:

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/node
```

**Headers:** sessionid

**Returns**: A JSON list of the node mapping to the job tasks.

#### 7.5- removeNodes endpoint:

**Description**: Remove nodes associations

**Path**:

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/node
```

**Headers:** sessionid

**Body:** A JSON body of the node IDs:

```json
[
    "<NODE_ID>",
    "<NODE_ID>", 
]
```

**Returns**: True if the node was successfully removed, false otherwise.