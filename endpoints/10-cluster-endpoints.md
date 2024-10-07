### 10.1- DefineCluster endpoint:

**Description**: This endpoint is used to define a Kubernetes cluster deployment.
Script templates for configuring the deployment workflow are available [here](https://github.com/ow2-proactive/scheduling-abstraction-layer/tree/master/docker/scripts). They can be modified to incorporate user-defined Kubernetes installation scripts, and for public clouds they need to have installed and set the network component. Additionally, they enable the installation of various software components within the cluster. Environmental variables required for specific configurations, along with their values, can be passed as part of the cluster definition. Before using this endpoint, ensure that cloud or edge nodes are added and selected for deployment execution.

 
**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cluster
```

**Headers:** sessionid

**Body:** A JSON body following this format:

```json
[
  {
    "name": "{{cluster_name}}",
    "master-node": "{{master_name}}",
    "nodes": [
      {
        "nodeName": "{{master_name}}",
        "nodeCandidateId": "{{MasterNodeCandidate}}",
        "cloudId": "{{cloud_name}}"
      },
      {
        "nodeName": "{{worker_name}}",
        "nodeCandidateId": "{{WorkerNodeCandidate}}",
        "cloudId": "{{cloud_name}}"
      }
    ],
    "env-var":{
      //here add any env variable needed for the cluster in the form "ENV_VAR_NAME":"test-ENV_VAR_value"
      // for instance: "APPLICATION_ID":"0fb75671-8955-4c06-9a8c-d397b29e3894"
      "{{env_var_name1}}":"{{env_var_value1}}",
      "{{env_var_name2}}":"{{env_var_value2}}"
    }
  }
]
```

**Reply:** Boolean


When defining nodes within a cluster, each node should be represented as an instance of
the [IaasNode](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/IaasNode.java) class, 
which can be imported from the `sal-common` package. An example of the node structure is as follows:

```json
[
  {
    "nodeName": "master-node-name",
    "nodeCandidateId": "{{MasterNodeCandidate}}",
    "cloudId": "given cloud name or 'edge'"
  }
  
]
```

***Important Considerations:***

- *Cluster Name Length:*
The cluster name should be kept relatively short, as it will be included in the names of the cluster nodes. Note that many cloud providers impose restrictions on the length of node names, so it's essential to ensure compatibility with these limits.

- *Node Name Requirements:*
Node names must adhere to Internet hostname conventions, allowing only lowercase letters, digits, and hyphens, and must start with a letter.
Node names must be globally unique across all clusters defined within the system.

- *Node Candidate ID (`nodeCandidateId`):*
For edge devices, the `nodeCandidateId` is returned when [registering the device](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/4-edge-endpoints.md#41--registernewedgenode-endpoint). 
It can be retrieved for both, edge and cloud nodes, by querying system using [findNodeCandidates](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/7-node-endpoints.md#71--findnodecandidates-endpoint) endpoint.

- *Cloud ID (cloudId):*
This value should reflect the name of the [registered cloud provider](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/2-cloud-endpoints.md#21--addcloud-endpoint). 
For edge devices, the cloudId should be set to `EDGE`.

By following these guidelines, you can ensure that your node configurations are compatible with both the SAL platform and various cloud provider requirements.



### 10.2- DeployCluster endpoint:

**Description**: The deploy cluster endpoint send the defined cloud for the deployment on ProActive server. 
The deployment 



**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/....
```

**Headers:** sessionid

**Body:** Json input following this format:

```json
[
  
  
]
```


**Reply:** Error code, 0 if no Errors



### 10.2- DeployCluster endpoint:

**Description**:



**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/....
```

**Headers:** sessionid

**Body:** Json input following this format:

```json
[
  
  
]
```


**Reply:** Error code, 0 if no Errors