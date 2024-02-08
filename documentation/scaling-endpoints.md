#### 8.1- Scale out task endpoint:

**Description**: Register a set of nodes as an operation for scale out
For more details check: [Deployment class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/Deployment.java), [ScalingService class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/service/ScalingService.java), and [ScalingRest class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/rest/ScalingRest.java).
**Path**:

```
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/scale/<JOB_ID>/<TASK_NAME>/out
```

**Path Variable:** The job identifier `<JOB_ID>` and the task name  `<TASK_NAME>`

**Headers:** sessionid

**Body:**
A JSON list of the node names: 
```json
[
"node_name_1", 
"node_name_2"
]

```

**Returns**: True if the scaling was successful, false otherwise.

* * *

#### 8.2- Scale in task endpoint:

**Description**: Unregister a set of nodes as a scale in operation
For more details check: [Deployment class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/Deployment.java), [ScalingService class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/service/ScalingService.java), and [ScalingRest class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/rest/ScalingRest.java).
**Path**:

```
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/scale/<JOB_ID>/<TASK_NAME>/in
```

**Path Variable:** The job identifier `<JOB_ID>` and the task name  `<TASK_NAME>`

**Headers:** sessionid

**Body:**
A JSON list of nodes to be removed
```json
[
"node_name_1", 
"node_name_2"
]

```

**Returns**: True if the scaling was successful, false otherwise.
