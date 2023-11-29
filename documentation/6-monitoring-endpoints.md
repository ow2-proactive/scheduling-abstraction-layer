#### 6.1- addEmsDeployment endpoint:

**Description**: Add an EMS deployment to a defined node

**Path**:

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/monitor
```

**Headers:** sessionid

**Body:** A JSON body following this format:

```json
{
    "nodeNames": [
        "nodename"
    ],
    "authorizationBearer": "SOME_BEARER",
    "isPrivateIp": true
}
```

In this case the node name refers to the component name defined in the job description.

**Returns**:

#### 6.2- getMonitorsList endpoint:

**Description**: Get the list of all available EMS deployment monitor requests

**Path**:

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/monitor
```

**Headers:** sessionid

**Returns**: A JSON list of all the defined EMS deployments.