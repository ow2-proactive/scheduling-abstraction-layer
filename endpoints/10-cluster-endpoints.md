### 10.1- DefineCluster endpoint:

**Description**: This endpoint is used to define a Kubernetes cluster deployment.
Script templates for configuring the deployment workflow are available [here](https://github.com/ow2-proactive/scheduling-abstraction-layer/tree/master/docker/scripts). They can be modified to incorporate user-defined Kubernetes installation scripts, and for public clouds they need to have installed and set the network component. Additionally, they enable the installation of various software components within the cluster. Environmental variables required for specific configurations, along with their values, can be passed as part of the cluster definition. Before using this endpoint, ensure that cloud or edge nodes are added and selected for deployment execution.

Cluster definition is the instance of class [Cluster](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/ClusterDefinition.java) that you can import from the `sal-common` package

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cluster
```

**Headers:** `sessionid`

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

**Reply:** Boolean, `true` if successful


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

**Headers:** `sessionid`

**Body:** None


**Reply:** Boolean, `true` if successful



### 10.3- GetCluster endpoint:

**Description**:
This endpoint retrieves detailed information about the Kubernetes cluster deployment. It provides real-time status updates on the deployment progress for each individual node, as well as the overall cluster. This information allows users to monitor the deployment process, track node-specific statuses, and verify the current state of the entire cluster.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cluster/{{cluster_name}}
```

**Headers:** `sessionid`

**Body:** None

**Reply:** JSON format represented with the [ClusterNodeDefinition](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/ClusterNodeDefinition.java) and status values corresponding to ones observed in ProActive dashboard.


### 10.4- ManageApplication endpoint:

**Description**:
This endpoint is used to deploy and manage applications within a specific Kubernetes cluster, utilizing kubectl, KubeVela, or Helm. Upon initiating a deployment, the endpoint generates an application deployment workflow within the designated cluster, which can then be monitored and managed directly in ProActive.

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cluster/{{cluster_name}}/app
```

**Headers:** `sessionid`

**Body:** Json input following this format:

```json
[
  {
    "appFile" : "---\napiVersion: \"core.oam.dev/v1beta1\"\nkind: \"Application\"\nmetadata:\n  name: \"dummy-app-deploy\"\nspec:\n  components:\n  - name: \"{{app_component_name}}\"\n    type: \"webservice\"\n    properties:\n      cpu: \"2.0\"\n      memory: \"2048Mi\"\n      image: \"docker.io/rsprat/mytestrepo:v1\"\n      imagePullPolicy: \"Always\"\n      cmd:\n      - \"python\"\n      - \"worker.py\"\n      env:\n      - name: \"mqtt_ip\"\n        value: \"broker.hivemq.com\"\n      - name: \"mqtt_port\"\n        value: \"1883\"\n      - name: \"mqtt_subscribe_topic\"\n        value: \"$share/workers/neb/test/input\"\n      - name: \"nebulous_ems_ip\"\n        valueFrom:\n          fieldRef:\n            fieldPath: \"status.hostIP\"\n      - name: \"nebulous_ems_port\"\n        value: \"61610\"\n      - name: \"nebulous_ems_user\"\n        value: \"aaa\"\n      - name: \"nebulous_ems_password\"\n        value: \"111\"\n      - name: \"nebulous_ems_metrics_topic\"\n        value: \"realtime.job_process_time_instance\"\n    traits:\n    - type: \"scaler\"\n      properties:\n        replicas: 2\n  policies:\n  - name: \"target-default\"\n    type: \"topology\"\n    properties:\n      namespace: \"default\"\n  workflow:\n    steps:\n    - name: \"deploy2default\"\n      type: \"deploy\"\n      properties:\n        policies:\n        - \"target-default\"\n",
    "packageManager" : "kubevela", // kubectl or helm
    "appName" : "{{app_name}}",
    "action" : "apply",
    "flags" : ""
  }
]
```


**Reply:**  A long of the ProActive Job ID

Field `appName` must be valid as a filename; therefore, spaces, quotes, and other special characters should not be used.

`action` value `apply` can be used both for the initial deployment and for ongoing application management. For example, you can adjust the number of replicas to scale the application according to demand.

Note that from the `appFile` the value of `{{app_component_name}}` is used when calling LabelNode endpoint. Also, it is to include `\n` characters at the end of each line to indicate line breaks (JSON requirement).

#### 2.2- GetAllClouds endpoint:

### 10.5- DeleteCluster endpoint:

**Description**: This endpoint allows users to delete an existing Kubernetes cluster deployment. It removes all resources associated with the specified cluster, including nodes, network configurations, and any deployed applications. By using this endpoint, users can clean up and release the resources used by the cluster.



**Path:**

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cluster/{{cluster_name}}
```

**Headers:** `sessionid`

**Body:** None


**Reply:**  Boolean, `true` if successful


### 10.6- ScaleOut endpoint:

**Description**: This endpoint allows users to dynamically expand their Kubernetes cluster by adding new worker nodes. 
This scaling operation is based on existing worker node definitions and is critical when increasing the cluster's capacity to support more replicas for applications.
Each worker node is introduced using the existing worker node candidate, but is uniquely identified by its `nodeName`. The number of nodes added corresponds to the number of replicas the user wishes to create.
For example, if a user wants to increase the number of replicas from 1 to 3, they could call the ScaleOut endpoint to add two more worker nodes (e.g., `worker2`, `worker3`), ensuring the cluster can support the desired number of replicas.

After the ScaleOut operation, the new worker nodes and their status can be tracked using the GetCluster endpoint or monitored in the ProActive. The created workflows correspond to the one which is used to initial node deployments in cluster, using user defined scripts. 
When the new nodes are deployed, the LabelNodes endpoint should be used to label these nodes (e.g., `worker2_name`, `worker3_name`) for identification and management purposes.
To complete ScaleOut process, ManageApplication endpoint is to be called, with updated number of the replicas (e.g. by adding two new replicas).


**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cluster/{{cluster_name}}/scaleout
```

**Headers:** `sessionid`

**Body:** Json input following this format:

```json
[
  //scale out using WorkerNodeCandidate which is part of the cluster and introducing the new node name
  {
    "nodeName": "{{new_worker2_name}}",
    "nodeCandidateId": "{{WorkerNodeCandidate}}",
    "cloudId": "{{cloud_name}}"
  },
  {
    "nodeName": "{{new_worker3_name}}",
    "nodeCandidateId": "{{WorkerNodeCandidate}}",
    "cloudId": "{{cloud_name}}"
  }
  // ....
]
```

**Reply:** JSON format represented with the [ClusterNodeDefinition](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/ClusterNodeDefinition.java) and status values corresponding to ones observed in ProActive dashboard.

Note that `nodeName` should be unique name for each new worker node to be added. It must follow naming conventions (no spaces, special characters, or uppercase letters), as it will be used as part of the cluster node identifier.

### 10.7- ScaleIn endpoint:

**Description**:
This endpoint allows users to remove specific worker nodes from a Kubernetes cluster. This operation is essential for efficiently managing cluster resources, especially when scaling down applications or reconfiguring the cluster architecture. By invoking this endpoint, you can effectively decommission nodes that are no longer needed or are underutilized.

Before invoking the ScaleIn endpoint, ensure that the workloads on `worker_name` have been migrated to other nodes, by using the LabelNodes endpoint to mark it as unavailable and using ManageApplication endpoint to reduce the number of the application replicas.

After the ScaleIn operation, the removal of worker nodes and their status can be tracked using the GetCluster endpoint or monitored in the ProActive. The created workflows correspond to the one which is used to remove nodes during DeleteCluster operation.,

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cluster/{{cluster_name}}/scalein
```

**Headers:** `sessionid`

**Body:** Json input following this format:

```json
[
  "{{worker_name}}",
  "{{worker_name2}}"
]
```

**Reply:** JSON format represented with the [ClusterNodeDefinition](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/ClusterNodeDefinition.java) and status values corresponding to ones observed in ProActive dashboard.

Note that the worker names should correspond to existing ones in the cluster, otherwise the operation will be rejected. 

### 10.8- LabelNode endpoint:

**Description**: This endpoint allows users to manage node labels within a Kubernetes cluster by adding, modifying, or removing labels. Labels are key-value pairs that categorize nodes, making it easier to target specific nodes for application deployment, scaling, or management tasks. 
Using LabelNodes, you can dynamically adjust labels on worker nodes as you manage scaling operations. During ScaleOut, nodes are labeled to mark them as available for application deployments. Before invoking ScaleIn, labels can be updated to indicate that certain nodes should no longer be scheduled for application workloads, preparing them for safe removal.

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cluster/{{cluster_name}}/label
```

**Headers:** `sessionid`

**Body:** Json input following this format:

```json
[
  // to add lable it is to use value 'yes' and to remove 'no' 
  {
    "{{worker2_name}}":"{{domain_prefix}}/{{app_component_name}}=yes",
    "{{worker_name}}":"{{domain_prefix}}/{{app_component_name}}=no"
  }
]
```

**Reply:** ProActive jobID

- _Node Name (Key)_: The name of the node to which the label is being added or updated. Each node name should correspond to a node deployed in the cluster.
Example node names might include `worker2_name` when labeling for workload readiness or `worker_name` when preparing for scaling down.
- _Label (Value)_: The label applied to the node is structured in a `key=value` format, where:

  - `key`: Typically consists of a custom domain prefix followed by the application component name which is passed in ManageApplication endpoint. `domain_prefix` functions as a unique namespace to avoid conflicts, especially in multi-tenant environments. It ensures that the label applies specifically to your application, component, or organization.
  - `value`: Use `yes` to indicate that the node is ready for application deployment and `no` to mark it for removal during scaling operations.

***Usage scenarios:***
  - _Scaling Out_:
  Use the ScaleOut endpoint to add new worker nodes to the cluster.
  Label the new node (e.g., `worker2_name`) with `yes`, which signals that the node is available for application workloads.
  Deploy applications using DeployApplication, which targets nodes labeled with `yes`. This ensures that replicas are scheduled only on nodes intended for the application.
  - _Preparing for Scaling In_:
  Update the label on the original node (e.g., `worker_name`) to `no` before initiating the ScaleIn process. This marks the node as unavailable for new application replicas. It is also to call ManageApplication to remove application replicas from the node. 
  Finally, call ScaleIn with the node name in the following format, which safely removes the node from the cluster:

***Important Notes:***
  Ensure that the environmental variables and application components are consistent across all endpoints for smooth scaling and management operations.
  
