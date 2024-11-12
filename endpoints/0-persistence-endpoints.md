#### 0.1 - Clean All Endpoint
**Description**: Cleans all clusters, clouds, and edge devices in the SAL and undeploys them from the ProActive server.
Note that all asynchronous processes related to cloud node candidate synchronization should be completed; otherwise, clouds will not be removed. To validate, please call the [isAnyAsyncNodeCandidatesProcessesInProgress](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/2-cloud-endpoints.md#23--isanyasyncnodecandidatesprocessesinprogress-endpoint) endpoint.
In a case that any removal of the cluster, cloud or edge resources fail the database cleanup will not be initiated.

**Path:**

```url
🟡 DELETE {{protocol}}://{{sal_host}}:{{sal_port}}/sal/persistence/clean
```

**Headers:**
* `sessionid`: The session ID obtained from the Connect endpoint.

**Body:** None

**Reply:** `Boolean` - Returns `true` if all resources were cleaned successfully, `false` otherwise.

_NOTE:_ If a `NotConnectedException` occurs (HTTP 500 error), it indicates that the connection to the ProActive server was lost. Reconnect using the Connect endpoint to obtain a new session ID.

---

#### 0.2. - Clean All Clusters Endpoint
**Description**: Cleans all cluster resources in SAL and undeploys cluster nodes on the ProActive server.

**Path:**

```url
🟡 DELETE {{protocol}}://{{sal_host}}:{{sal_port}}/sal/persistence/clean/clusters
```

**Headers:**
* `sessionid`: The session ID obtained from the Connect endpoint.

**Body:** None

**Reply:** `Boolean` - Returns `true` if all clusters were successfully cleaned, `false` otherwise.

---

#### 0.3 - Clean All Clouds Endpoint
**Description**: Cleans all cloud resources in SAL and undeploys cloud nodes on the ProActive server.
Note that all asynchronous processes related to cloud node candidate synchronization should be completed; otherwise, clouds will not be removed. To validate, please call the [isAnyAsyncNodeCandidatesProcessesInProgress](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/2-cloud-endpoints.md#23--isanyasyncnodecandidatesprocessesinprogress-endpoint) endpoint.
It is advised to remove or reconfigure existing clusters before making this call, as any nodes deployed in the clouds will be removed.


**Path:**

```url
🟡 DELETE {{protocol}}://{{sal_host}}:{{sal_port}}/sal/persistence/clean/clouds
```

**Headers:**
* `sessionid`: The session ID obtained from the Connect endpoint.

**Body:** None

**Reply:** `Boolean` - Returns `true` if all clouds were successfully cleaned, `false` otherwise.

---

#### 0.4 - Clean All Edge Devices Endpoint
**Description**: Deregisters all edge devices from the ProActive server. It is advised to remove or reconfigure existing clusters before making this call, as any nodes deployed in the edge devices will be removed along with the clusters.


**Path:**

```url
🟡 DELETE {{protocol}}://{{sal_host}}:{{sal_port}}/sal/persistence/clean/edges
```

**Headers:**
* `sessionid`: The session ID obtained from the Connect endpoint.

**Body:** None

**Reply:** `Boolean` - Returns `true` if all edge devices were successfully deregistered, `false` otherwise.

---

#### 0.5 - Clean SAL Database Endpoint
**Description**: Cleans all database entries in the SAL system.

**Path:**

```url
🟡 DELETE {{protocol}}://{{sal_host}}:{{sal_port}}/sal/persistence/clean/SALdatabase
```

**Headers:**
* `sessionid`: The session ID obtained from the Connect endpoint.

**Body:** None

**Reply:** None
