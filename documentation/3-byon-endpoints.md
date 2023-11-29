#### 3.1- RegisterNewByonNode endpoint:

**Description**: Register new BYON nodes passed as ByonDefinition object

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/byon/<JOB_ID>?automate=<VALUE>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Request Parameter:** `automate` parameter that can take a value of 0 or 1. If the value is set to 1, the ProActive node agent will be automatically installed.

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
            "value": "<PRIVATE_IP>"
        }
    ],
    "nodeProperties": {
        "providerId": "1",
        "numberOfCores": 1,
        "memory": 1,
        "disk": 1.0,
        "operatingSystem": {
            "operatingSystemFamily": "UBUNTU",
            "operatingSystemArchitecture": "AMD64",
            "operatingSystemVersion": 1804
        },
        "geoLocation": {
            "city": "Warsaw",
            "country": "Poland",
            "latitude": 52.237049,
            "longitude": 21.017532
        }
    }
}
```

#### 3.2- GetByonNodes endpoint:

**Description**: An endpoint to get all the available BYON nodes.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/byon/<JOB_ID>
```

**Path Variable:** The Job ID.

**Headers:** sessionid

**Body:** None

#### 3.3- AddByonNodes endpoint:

**Description**: Adding BYON nodes to a job component.

**Path:**

```url
🔵 PUT {{protocol}}://{{sal_host}}:{{sal_port}}/sal/byon/<JOB_ID>
```

**Path Variable:** The Job ID.

**Headers:** sessionid

**Body:**

```Json
{
    "<BYON_ID>":"<DEPLOYMENT_NAME>/<COMPONENT_NAME>"
}
```

#### 3.4- DeleteByonNoade endpoint:

**Description**: Remove Byon node.

**Path:**

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/byon/<BYON_ID>
```

**Path Variable:** BYON\_ID of the node to be deleted.

**Headers:** sessionid

**Body:** None
