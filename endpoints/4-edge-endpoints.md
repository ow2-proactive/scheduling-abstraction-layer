### 4.1- RegisterNewEdgeNode endpoint:

**Description**:
This endpoint is used to register new Edge nodes, which are passed as an [EdgeDefinition](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/EdgeDefinition.java)  object. The information provided allows the node to be integrated into the ProActive environment, where it can be used for various deployments and managed within clusters.

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
    "price": "{{price}}",
    "providerId": "{{providerID}}",
    "cores": "{{cores}}",
    "cpuFrequency": "{{cpuFrequency}}",
    "ram": "{{ram}}",
    "disk": "{{disk}}",
    "fpga": "{{fpga}}",
    "gpu": "{{gpu}}",
    "operatingSystem": {
      "operatingSystemFamily": "{{OS_name}}",
      "operatingSystemArchitecture": "{{OS_architecture}}",
      "operatingSystemVersion": "{{OS_version}}"
    },
    "geoLocation": {
      "city": "{{edge_city}}",
      "country": "{{edge_country}}",
      "latitude": "{{edge_latitude}}",
      "longitude": "{{edge_longitude}}"
    }
  },
  "port": "{{edge_port}}",
  "jobId": "{{jobId}}",
  "systemArch": "{{edge_architecture}}",
  "scriptURL": "https://www.google.com",
  "jarURL": "{{jar_url}}"
}
```

**Reply:** The response will be a JSON object containing information about the registered edge node, including:
- The edge node ID (used for edge node [removal](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/4-edge-endpoints.md#44--deleteedgenode-endpoint)).
- The node candidate ID (used for deployment).
- Information registered in node candidate for hardware, location, and image that represent the device.

The fields are defined as:
- `name`(String): The name of the edge node, used for identification and management within the cluster.
- `loginCredential`: Contains authentication details for accessing the edge node. The username and password are required for SSH access, with an option for a privateKey instead of a password.
- `ipAddresses`: A list of IP addresses associated with the node, including both PUBLIC_IP and PRIVATE_IP with IP Version specified as V4.
- `nodeProperties`: Represents the properties being transmitted to a node candidate, reflecting the attributes of the registered edge device.
  - `price`(Double): The price of the edge node source.
  - `providerId` (String): The unique identifier of the provider.
  - `cores`(Integer): The number of CPU cores the node possesses.
  - `cpuFrequency`(Double): The CPU frequency in GHz.
  - `ram`(Long): The hardware's RAM memory in MB.
  - `disk`(Double): The hardware's storage space in GB.
  - `fpga`(Integer): The number of FPGAs (Field-Programmable Gate Arrays) available on the node.
  - `gpu`(Integer): The number of GPUs (Graphics Processing Units) available on the node.
  - `operatingSystem`: Information about the OS, including Family, Architecture, and Version.
  - `geoLocation`: The physical location details, such as city, country, latitude, and longitude of the edge node.
- `port` (String): The port number on which the edge node is accessible.
- `jobId`(String): The ProActive Job ID associated with the edge node. Set to `"0"` or `"any"`, or `null`, if no job is linked.
- `systemArch`(String): The system architecture, which must be one of `"AMD"`, `"ARMv8"`, or `"ARMv7"`.
- `scriptURL`(String): A URL pointing to a script required for setting up the node.
- `jarURL`(String): The URL for the node's executable `.jar` file, which corresponds to the `systemArch`.

Each system architecture requires a specific `jarURL` for node execution, available from your ProActive installation. To obtain these `.jar` files, access the ProActive Resource Manager portal and go to _Portal -> Launch a Node_. Here are examples for various architectures:


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

Replace the domain `try.activeeon.com` with your own ProActive IP address and port as needed.

Current execution agents (.jars) are approximately 100 MB and require about 500 MB of resources.
For small devices, reduced agents can be provided, but this may result in limited ProActive features.


*Searching Node Candidate representing Edge Node by its name:* After registering an edge device, you can [search for its node candidate](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/7-node-endpoints.md#71--findnodecandidates-endpoint) using the `edge_name` as part of the Attribute requirement `hardware` with value `name`.



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

**Headers:** `sessionid`

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

NOTE: It is advised to remove or reconfigure existing clusters before making this call, as any nodes deployed in the edge node sent for removal will be undeployed and cluster will not operate correctly.
