#### 4.1- RegisterNewEdgeNode endpoint:

**Description**: Register new Edge nodes passed as EdgeDefinition object

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
  "jarURL": "{{jar_url}}" // e.g. "https://try.activeeon.com/rest/node.jar"
}
```
**Reply:** JSON body containing information about the registered edge node, including the ID of the edge node that needs to be used for its removal, the ID of the node candidate representing this device, as well as the IDs set for the hardware, location, and image representing this device.

Each edge node can be associated with a ProActive job. In case it is not associated with a ProActive job, use the values **"0"** or **"any"** for `jobId`.

The supported system architectures, their values, and their `.jar` URLs are as follows:
- **"AMD"** - for AMD64 (x86_64) architecture (Intel x86_64) with node.jar from ProActive RM ("https://try.activeeon.com/rest/node.jar"). Replace the domain name (try.activeeon.com) with your IP address, including the port.
- **"ARMv8"** - for 64-bit ARM processors : _node.jar_ TBD
- **"ARMv7"** - for 32-bit ARM processors : _node.jar_ TBD

#### 4.2- GetEdgeNodes endpoint:

**Description**: An endpoint to get all the available Edge nodes.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/edge/{{jobId}}
```

**Path Variable:** The `jobId`. In case it is not associated with a ProActive job, use the values **"0"** or **"any"** for `jobId`

**Headers:** `sessionid`

**Body:** None

**Reply:** JSON body containing information about the registered edge nodes with same information as returned during registration process.

#### 4.3- AddEdgeNodes endpoint:

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

#### 4.4- DeleteEdgeNode endpoint:

**Description**: Remove Edge node.

**Path:**

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/edge/{{EdgeNodeID}}
```

**Path Variable:** `id` of edge node which is to be deleted, which is obtained during registration or by calling 4.2. GetEdgeNodes endpoint.

**Headers:** `sessionid`

**Body:** None

**Reply:** `true` if the edge device is successfully removed.
