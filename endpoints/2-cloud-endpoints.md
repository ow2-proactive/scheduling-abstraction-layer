#### 2.1- AddCloud endpoint:

**Description**: An endpoint to define a cloud infrastructure to SAL, This will allow SAL to asynchronously retrieve the offers (cloud images and node candidates) in the background.
Note that cloud credentials are validated only during async process.

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud
```

**Headers:** sessionid

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
**Reply:** Error code, 0 if no Errors

NOTE: To provide the RSA private key correctly for JSON, you'll need to include `\n` characters at the end of each line to indicate line breaks.
#### 2.2- GetAllClouds endpoint:

**Description**: An endpoint to get all the defined clouds in SAL.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud
```

**Headers:** sessionid

**Body:** None

**Reply:** JSON output containing cloud definitions

#### 2.3- isAnyAsyncNodeCandidatesProcessesInProgress endpoint:
**Description**: An endpoint to check if there is any asynchronous process ongoing to retrieve the cloud images or node candidates.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/async
```

**Headers:** sessionid

**Body:** None

**Reply:**  Boolean, _True_ if there is async process ongoing and _False_ otherwise


#### 2.4- GetCloudImages endpoint:

**Description**: An endpoint to get all the retrieved images for all clouds or a specific cloud.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/images
```

**Path Variable (optional):** cloudid = {{cloud_name}}

**Headers:** sessionid

**Body:** None

**Reply:** JSON output containing image definitions

#### 2.5- GetCloudLocation endpoint:

**Description**: An endpoint to get all the retrieved locations (regions) for all clouds or a specific cloud.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/location
```

**Path Variable (optional):** cloudid = {{cloud_name}} -> TBD: this is not implemented yet

**Headers:** sessionid

**Body:** None

**Reply:** JSON output containing location definitions

#### 2.6- GetCloudHardware endpoint:

**Description**: An endpoint to get all the retrieved hardware (vmTypes) for a specific cloud.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/hardware
```

**Path Variable (optional):** cloudid = {{cloud_name}} -> TBD: this is not implemented yet

**Headers:** sessionid

**Body:** None

**Reply:** JSON output containing hardware definitions

#### 2.7- RemoveClouds endpoint:

**Description**: An endpoint to get all the remove a list of defined clouds.

**Path:**

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/remove
```

**Headers:** sessionid

**Body:**

```json
[
  "{{cloud_name}}",
  "{{cloud_name2}}"
]
```
**Reply:**  Boolean, True if cloud infrastructure was removed. False, otherwise.