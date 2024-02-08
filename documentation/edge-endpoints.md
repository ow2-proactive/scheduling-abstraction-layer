#### 4.1- RegisterNewEdgeNode endpoint:

**Description**: Register new Edge nodes passed as EdgeDefinition object

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/edge/<JOB_ID>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:**

*   sessionid

**Body:**

```json
{
    "name": "<USER_DEFINED_NAME>",
    "loginCredential": {
        "username": "<SSH_USERNAME>",
        "password": "<SSH_PASSWORD>",
        "privateKey": ""
    },
    "ipAddresses": [
        {
            "IpAddressType": "PUBLIC_IP",
            "IpVersion": "V4",
            "value": "<PUBLIC_IP>"
        },
        {
            "IpAddressType": "PRIVATE_IP",
            "IpVersion": "V4",
            "value":  "<PRIVATE_IP>"
        }
    ],
    "nodeProperties": {
        "providerId": "1",
        "numberOfCores": 1,
        "memory": 1,
        "disk": 1.0,
        "operatingSystem": {
            "operatingSystemFamily": "UBUNTU",
            "operatingSystemArchitecture": "ARMv8",
            "operatingSystemVersion": 1804
        },
        "geoLocation": {
            "city": "Warsaw",
            "country": "Poland",
            "latitude": 52.237049,
            "longitude": 21.017532
        }
    },
    "systemArch": "ARMv8",
    "scriptURL": "<STARTUP_SCRIPT_URL>",
    "jarURL": "<JAR_URL>"
}
```

#### 4.2- GetEdgeNodes endpoint:

**Description**: An endpoint to get all the available Edge nodes.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/edge/<JOB_ID>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Body:** None

#### 4.3- AddEdgeNodes endpoint:

**Description**: Adding Edge nodes to a job component.

**Path:**

```url
🔵 PUT {{protocol}}://{{sal_host}}:{{sal_port}}/sal/edge/<JOB_ID>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Body:**

```Json
{
    "<Edge_ID>":"<DEPLOYMENT_NAME>/<COMPONENT_NAME>"
}
```

#### 4.4- DeleteEdgeNoade endpoint:

**Description**: Remove Edge node.

**Path:**

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/edge/<BYON_ID>
```

**Path Variable:** EDGE\_ID of the node to be deleted.

**Headers:** sessionid

**Body:** None
