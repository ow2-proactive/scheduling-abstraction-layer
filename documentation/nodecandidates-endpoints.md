#### 7.1- Filter node candidates endpoint:

**Description**: A node candidate is a combination of an image, location, and hardware that are feasiable together. In the case of a Cloud provider SAL will analyze all these possible combinations and create a list of node candidates that are ready to be servered to any logic unit. In this endpoint we define a way to retrieve and filter these nodes using `NodeTypeRequirement` and `AttributeRequirement`

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
POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodecandidates/filter
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
        "value": "4096"
    }
]
```

**Returns**: A JSON list of node candidates:

```json
[
    {
        "id": "2c9280838d55ecbc018d55ef4ad80014",
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
        "nodeId": "2c9280838d55ecbc018d55ef4ade0015",
        "environment": null
    }
]
```

#### 7.2- Order filtered node candidates endpoint:

**Description**: Order the filtered node candidates according to a ranked node map

**Path**:

```url
POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/nodecandidates/orderfiltered
```

**Headers:** sessionid

**Body:** A JSON body following this format:

```json
{
    "402890838d84a8a3018d84a9b3af0014": {"score" : 100, "rank" : 0},
    "402890838d84a8a3018d84a9c2f100d6": {"score" : 30.4, "rank" : 2},
    "402890838d84a8a3018d84a9d08b0198": {"score" : 70.9, "rank" : 1},
    "402890838d84a8a3018d84a9d3a201cc":{"score" : 0, "rank" : 3}
}
```

**Returns**: A JSON list of node candidates
