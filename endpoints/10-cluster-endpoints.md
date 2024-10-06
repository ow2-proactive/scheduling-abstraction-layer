#### 10.1- DefineCluster endpoint:

**Description**: This endpoint is used to define a Kubernetes cluster deployment.
Script templates for configuring the deployment workflow are available [here](https://github.com/ow2-proactive/scheduling-abstraction-layer/tree/master/docker/scripts). They can be modified to incorporate user-defined Kubernetes installation scripts. Additionally, they enable the installation of various software components within the cluster. Environmental variables required for specific configurations, along with their values, can be passed as part of the cluster definition. Before using this endpoint, ensure that cloud or edge nodes are added and selected for deployment execution.



 
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

