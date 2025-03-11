#### 2.1- AddCloud endpoint:

**Description**: An endpoint to define a cloud infrastructure to SAL, This will allow SAL to asynchronously retrieve the offers (cloud images and node candidates) in the background.
Note that cloud credentials are validated only during async process.

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud
```

**Headers:** `sessionid`

**Body:** Json input following this format:

*   For OpenStack:

```json
[
  {
    "cloudId": "{{cloud_name}}",
    "cloudProviderName": "openstack",
    "cloudType": "PRIVATE",
    "subnet": null,
    "securityGroup": "{{os-securityGroup}}",
    "sshCredentials": {
      "username": "{{os-username}}",
      "keyPairName": "{{os-keypair}}",
      "privateKey": null
    },
    "endpoint": "{{os-auth_url}}",
    "scope": {
      "prefix": "project",
      "value": "{{os-projectName}}"
    },
    "identityVersion": "{{os-identity-api-version}}",
    "defaultNetwork": "{{os-defaultNetwork}}",
    "credentials": {
      "user": "{{os-user}}",
      "secret": "{{os-secret}}",
      "domain": "{{os-domain}}"
    },
    "blacklist": null
  }
]
```

*   For AWS cloud:

```json
[
  {
    "cloudId": "{{cloud_name}}",
    "cloudProviderName": "aws-ec2",
    "cloudType": "PUBLIC",
    "subnet": null,
    "securityGroup": "{{aws-securityGroup}}",
    "sshCredentials": {
      "username": "{{aws-username}}",
      "keyPairName": "{{aws-keypair}}",
      "privateKey": "{{aws-privatekey}}"
    },

    "endpoint": null,
    "scope": {
      "prefix": null,
      "value": null
    },
    "identityVersion": null,
    "defaultNetwork": null,
    "credentials": {
      "user": "{{aws-user}}",
      "secret": "{{aws-secret}}",
      "domain": null
    },
    "blacklist": null
  }
]
```

*   For Azure cloud:

```json
[
  {
    "cloudId": "{{cloud_name}}",
    "cloudProviderName": "azure",
    "cloudType": "PUBLIC",
    "subnet": null,
    "securityGroup": null,
    "sshCredentials": {
      "username": "ubuntu",
      "keyPairName": null,
      "publicKey": "{{azure-publickey}}",
      "privateKey": "{{azure-password}}"
    },
    "endpoint": null,
    "scope": {
      "prefix": null,
      "value": null
    },
    "identityVersion": null,
    "defaultNetwork": null,
    "credentials": {
      "user": "{{azure-user}}",
      "secret": "{{azure-secret}}",
      "domain": "{{azure-domain}}",
      "subscriptionId": "{{azure-subscription_id}}"
    },
    "blacklist": null
  }
]
```

**Reply:** Error code, 0 if no Errors

- `cloudId` (string):
This is a unique identifier for the cloud infrastructure. Choose a unique descriptive name for easy identification, as it will be referenced by SAL.

- `cloudProviderName` (string):
The name of the cloud provider. For OpenStack, use `"openstack"`, and for AWS, use `"aws-ec2"`.

- `cloudType` (string):
Specifies whether the cloud infrastructure is `"PRIVATE"` (e.g., for OpenStack) or `"PUBLIC"` (e.g., for AWS).

- `subnet` (string or `null`):
This defines the specific subnet for your cloud infrastructure. If not needed, set this field to null.

- `securityGroup` (string):
The security group associated with this cloud configuration. Use the security group name applicable to your infrastructure’s security rules.

- `sshCredentials` (object):
Contains SSH access information for the cloud. For Open Stack and AWS should be defined on cloud provider side, while for Azure is automatically created as specified here. The required fields are:

    - `username` (string): The SSH username.
    - `keyPairName` (string): The name of the key pair used for SSH access.
    - `publicKey` (string or `null`): The public key in RSA format. If not required, use `null`.
    - `privateKey` (string or `null`): The private key in RSA format, with line breaks represented by `\n` for JSON compatibility. If not required, use `null`. For Azure, set it to the VM ssh password.

- `endpoint` (string or `null`):
    The authentication endpoint for the cloud provider. For OpenStack, use your specific authentication URL. AWS and Azure does not require this field, so it can be `null`.
- `scope` (object):
Defines the scope of the cloud access, typically is used for OpenStack. Contains:

  - `prefix` (string or `null`): For OpenStack, use `"project"`. Set to `null` for AWS.
  - `value` (string or `null`): Project name for OpenStack. For AWS, this should be `null`.

- `identityVersion` (string or `null`):
Specifies the version of the identity API. This is required for OpenStack but should be `null` for AWS.

- `defaultNetwork` (string or `null`):
Specifies the default network identifier, used primarily by OpenStack. Set this to `null` for AWS.

- `credentials` (object):
Contains authentication details for accessing the cloud. The fields are:

  - `user` (string): The cloud username or access key.
  - `secret` (string): The cloud password or secret access key.
  - `domain` (string or `null`): The domain for the cloud account, required by OpenStack. For AWS, set this to `null`.
  - `subscriptionId` (string or `null`): The subscription id for the cloud account, required by Azure. For AWS and OpenStack, set this to `null`.

- `blacklist` (string or `null`):
Allows you to specify any blacklisted regions (e.g. locations). Use `null` if not applicable.

#### 2.2- GetAllClouds endpoint:

**Description**: An endpoint to get all the defined clouds in SAL.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud
```

**Headers:** `sessionid`

**Body:** None

**Reply:** JSON output containing cloud definitions

#### 2.3- isAnyAsyncNodeCandidatesProcessesInProgress endpoint:
**Description**: An endpoint to check if there is any asynchronous process ongoing to retrieve the cloud images or node candidates.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/async
```

**Headers:** `sessionid`

**Body:** None

**Reply:**  Boolean, _True_ if there is async process ongoing and _False_ otherwise


#### 2.4- GetCloudImages endpoint:

**Description**: An endpoint to get all the retrieved images for all clouds or a specific cloud.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/images
```

**Path Variable (optional):** `cloudid` = {{cloud_name}}

**Headers:** `sessionid`

**Body:** None

**Reply:** JSON output containing image definitions

#### 2.5- GetCloudLocation endpoint:

**Description**: An endpoint to get all the retrieved locations (regions) for all clouds or a specific cloud.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/location
```

**Path Variable (optional):** `cloudid` = {{cloud_name}}

**Headers:** `sessionid`

**Body:** None

**Reply:** JSON output containing location definitions

#### 2.6- GetCloudHardware endpoint:

**Description**: An endpoint to get all the retrieved hardware (vmTypes) for a specific cloud.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/hardware
```

**Path Variable (optional):** `cloudid` = {{cloud_name}}

**Headers:** `sessionid`

**Body:** None

**Reply:** JSON output containing hardware definitions

#### 2.7- RemoveClouds endpoint:

**Description**: This endpoint removes a specified list of cloud infrastructures.

**Path:**

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/remove
```
**Path Variable (optional):** `preempt` = Boolean

- `true` - Removes all deployed nodes within the specified cloud infrastructures.
- `false`: (default) Removes only the specified cloud infrastructures without affecting deployed nodes.

**Headers (optional):** `sessionid`

**Body:**

```json
[
  "{{cloud_name}}",
  "{{cloud_name2}}"
]
```
**Reply:**  Boolean, `true` if cloud infrastructure was removed. `false`, otherwise.

NOTE: All asynchronous processes related to cloud node candidate synchronization should be completed; otherwise, clouds will not be removed. To validate, please call the [isAnyAsyncNodeCandidatesProcessesInProgress](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/2-cloud-endpoints.md#23--isanyasyncnodecandidatesprocessesinprogress-endpoint) endpoint.
It is advised to remove or reconfigure existing clusters before making this call, as any nodes deployed in the clouds sent for removal will be undeployed and cluster will not operate correctly.
