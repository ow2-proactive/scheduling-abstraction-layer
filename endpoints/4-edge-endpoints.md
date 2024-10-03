### 4.1- RegisterNewEdgeNode endpoint:

**Description**: Register new Edge nodes passed as [EdgeDefinition](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/EdgeDefinition.java) object

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/edge/register
```

**Headers:** `sessionid`

**Body:**

```json
{
  "name": "{{edge_name}}",
  "loginCredential": {
    "username": "{{ssh_username}}",
    "password": "{{ssh_password}}",
    "privateKey": ""
  },
  "ipAddresses": [
    {
      "IpAddressType": "PUBLIC_IP",
      "IpVersion": "V4",
      "value": "{{public_ip}}"
    },
    {
      "IpAddressType": "PRIVATE_IP",
      "IpVersion": "V4",
      "value": "{{private_ip}}"
    }
  ],
  "nodeProperties": {
    "providerId": "1",
    "numberOfCores": "{{cores}}", //e.g. "1"
    "memory": "{{memory}}", //e.g. "1"
    "disk": "{{disk}}", //e.g. "1.0"
    "operatingSystem": {
      "operatingSystemFamily": "{{OS_name}}",
      "operatingSystemArchitecture": "{{OS_arhitecture}}",
      "operatingSystemVersion": "{{OS_version}}"
    },
    "geoLocation": {
      "city": "{{edge_city}}",
      "country": "{{edge_county}}",
      "latitude": "{{edge_latitude}}", //e.g. "52.237049"
      "longitude": "{{edge_latitude}}" //e.g. "21.017532"
    }
  },
  "port": "{{edge_port}}",
  "jobId": "{{jobId}}", // use "0" or "any" when device is NOT associated with ProActive job
  "systemArch": "{{edge_architecture}}", // MUST be "AMD", "ARMv8" OR "ARMv7"
  "scriptURL": "https://www.google.com",
  "jarURL": "{{jar_url}}" 
}
```

**Reply:** The response will be a JSON object containing information about the registered edge node, including:
- The edge node ID (used for edge node [removal](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/4-edge-endpoints.md#44--deleteedgenode-endpoint)).
- The node candidate ID (used for deployment).
- Information registered in node candidate for hardware, location, and image that represent the device.

***Searching Node Candidate representing Edge Node by its name:*** After registering an edge device, you can [search for its node candidate](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/7-node-endpoints.md#71--findnodecandidates-endpoint) using the `edge_name` as part of the Attribute requirement `hardware` with value `name`.

***Job Association:*** Each edge node can be associated with a ProActive job. If the node is not linked to a specific job, use  `jobId:"0"` or `jobId:"any"`.

***Supported System Architectures and `jarURL`:*** The `jarURL`s needed for node execution are provided with your ProActive installation. To manually retrieve the correct `jarURL`, go to the ProActive Resource Manager portal and select *Portal -> Launch a Node*.

Here are examples of architecture-specific `jarURL`s, using ProActive's demo portal as a reference. Replace the domain `try.activeeon.com` with your own IP address and port as needed.

- **AMD (x86_64 architecture)**
```code
"systemArch":"AMD",
"jarURL": "https://try.activeeon.com/rest/node-amd-64.jar" //AMD 64 (smaller)
```

- **ARMv8 (64-bit ARM processors)**
```code
"systemArch": "ARMv8",
"jarURL": "https://try.activeeon.com/rest/node-arm-v8.jar" //ARM V8
```

- **ARMv7 (32-bit ARM processors)**
```code
"systemArch": "ARMv7",
"jarURL": "https://try.activeeon.com/rest/node-arm-v7.jar" //ARM V7
```
*Additional Notes:*
Current execution agents (node.jars) are approximately 100 MB and require about 500 MB of resources.
For small devices, reduced agents can be provided, but this may result in limited ProActive features.


### 4.2- GetEdgeNodes endpoint:

**Description**: An endpoint to get all the available Edge nodes.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/edge/{{jobId}}
```

**Path Variable:** The `jobId`. In case it is not associated with a ProActive job, use the values **"0"** or **"any"** for `jobId`

**Headers:** `sessionid`

**Body:** None

**Reply:** JSON body containing information about the registered edge nodes with same information as returned during registration process.

### 4.3- AddEdgeNodes endpoint:

**Description**: Adding Edge nodes to a job component.

**Path:**

```url
🔵 PUT {{protocol}}://{{sal_host}}:{{sal_port}}/sal/edge/<JOB_ID>
```

**Path Variable:** The Job ID.

**Headers:** sessionid

**Body:**

```Json
{
    "<Edge_ID>":"<DEPLOYMENT_NAME>/<COMPONENT_NAME>"
}
```

### 4.4- DeleteEdgeNode endpoint:

**Description**: Remove Edge node.

**Path:**

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/edge/{{EdgeNodeID}}
```

**Path Variable:** `id` of edge node which is to be deleted, which is obtained during registration or by calling 4.2. GetEdgeNodes endpoint.

**Headers:** `sessionid`

**Body:** None

**Reply:** `true` if the edge device is successfully removed.
