### 10.1- DefineCluster endpoint:

**Description**: This endpoint is used to define a Kubernetes cluster deployment.
Script templates for configuring the deployment workflow are available [here](https://github.com/ow2-proactive/scheduling-abstraction-layer/tree/master/docker/scripts). They can be modified to incorporate user-defined Kubernetes installation scripts, and for public clouds they need to have installed and set the network component. Additionally, they enable the installation of various software components within the cluster. Environmental variables required for specific configurations, along with their values, can be passed as part of the cluster definition. Before using this endpoint, ensure that cloud or edge nodes are added and selected for deployment execution.

Cluster definition is the instance of class [Cluster](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/ClusterDefinition.java) that you can import from the `sal-common` package

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

**Description**: This endpoint enables users to configure and deploy a Kubernetes cluster on the ProActive server. 
The deployment process involves:
- Cluster Definition Integration:
The cluster configuration is integrated into script templates to generate a ProActive workflow.
- Workflow Execution:
You can monitor the execution of the workflow via the GetCluster endpoint or directly within the [ProActive Dashboard and Scheduler](https://try.activeeon.com/).
- Resource Monitoring:
Resource nodes and their deployment progress can be tracked in the [ProActive Resource Manager](https://try.activeeon.com/).

This endpoint provides a robust testing environment for SAL users, 

SAL allow direct modifications to scripts for users. To test and customize both script-based and non-script-based tasks:
- Access the ProActive Workflow Studio:
From the ProActive Dashboard, open the workflow definition in the Workflow Studio. Here, you can edit scripts within workflow tasks and directly modify non-script-based tasks.
- Script and Task Modification:
Scripts can be customized to modify task behavior, adjust configurations, or add new functionalities. Additionally, non-scripted parts of the workflow (those tasks not loaded from scripts) can also be edited directly in the Workflow Studio. This allows users to test modifications immediately and verify their validity.
- Testing and Custom Workflow Requests:
Once modifications are made, you can execute the workflow to observe performance, view task logs, and ensure functionality. If any adjustments to non-scripted tasks are successful, users can request a customized version of the workflow for future deployments.
- Monitoring and Log Examination:
After executing updated tasks, you can monitor task execution and examine detailed logs for each individual task in the Scheduler, providing in-depth testing and debugging capabilities.


It’s essential to maintain consistency between any modifications to environmental variables and their corresponding values in the code.
By following these steps, users have complete flexibility to deploy, test, and refine both script-based and non-scripted tasks within their Kubernetes clusters on ProActive. The Workflow Studio offers a powerful environment for on-the-fly edits and full execution visibility.

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cluster/{{cluster_name}}
```

**Headers:** sessionid

**Body:** None


**Reply:** Boolean



### 10.3- GetCluster endpoint:

**Description**:
This endpoint retrieves detailed information about the Kubernetes cluster deployment. It provides real-time status updates on the deployment progress for each individual node, as well as the overall cluster. This information allows users to monitor the deployment process, track node-specific statuses, and verify the current state of the entire cluster.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cluster/{{cluster_name}}
```

**Headers:** sessionid

**Body:** None

**Reply:** JSON format represented with the [ClusterDefinition](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/ClusterDefinition.java) and status values corresponding to ones observed in ProActive dashboard.

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