#### 2.1- AddCloud endpoint:

**Description**: An endpoint to define a cloud to SAL, This will allow SAL to asyncrouniously retrieve the offers.

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud
```

**Headers:** sessionid

**Reply:** Error code, 0 if no Errors

**Body:** Json input following this format:

*   For AWS cloud:

```json
[
    {
        "cloudId": "<ID_SELECTED_BY_USER>",
        "cloudProviderName": "aws-ec2",
        "cloudType": "PUBLIC",
        "securityGroup": null,
        "subnet": null,
        "sshCredentials": {
            "username": null,
            "keyPairName": "<AWS_KEYPAIR_NAME>",
            "privateKey": null
        },
        "endpoint": null,
        "scope": {
            "prefix": null,
            "value": null
        },
        "identityVersion": null,
        "defaultNetwork": null,
        "credentials": {
            "user": "<AWS_USER>",
            "secret": "<AWS_SECRET>",
            "domain": null
        },
        "blacklist": null
    }
]
```

*   For OpenStack:

```json
[
{
        "cloudId": "<ID_SELECTED_BY_USER>",
        "cloudProviderName": "openstack",
        "cloudType": "PRIVATE",
        "securityGroup": null,
        "subnet": null,
        "endpoint": "<OS_AUTH_URL>",
        "scope": {
            "prefix": "project",
            "value": "<OS_PROJECT_NAME>"
        },
        "identityVersion": "<OS_IDENTITY_API_VERSION>",
        "defaultNetwork": null,
        "credentials": {
            "user": "<OS_USERNAME>",
            "secret": "<OS_PASSWORD>",
            "domain": "<OS_PROJECT_DOMAIN_NAME>"
        },
        "blacklist": null
    }
]
```

#### 2.2- GetAllClouds endpoint:

**Description**: An endpoint to get all the defined cloud to SAL.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud
```

**Headers:** sessionid

**Body:** None

#### 2.3- GetCloudImages endpoint:

**Description**: An endpoint to get all the retrieved images for a specific cloud.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/images?<CLOUD_ID>
```

**Path Variable:** the cloud ID.

**Headers:** sessionid

**Body:** None

#### 2.4- GetCloudLocations endpoint:

**Description**: An endpoint to get all the retrieved locations for a specific cloud.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/location?<CLOUD_ID>
```

**Path Variable:** the cloud ID.

**Headers:** sessionid

**Body:** None

#### 2.5- GetCloudHardwares endpoint:

**Description**: An endpoint to get all the retrieved hardwares (vmTypes) for a specific cloud.

**Path:**

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/hardware?<CLOUD_ID>
```

**Path Variable:** the cloud ID.

**Headers:** sessionid

**Body:** None

#### 2.6- RemoveClouds endpoint:

**Description**: An endpoint to get all the remove a list of defined clouds.

**Path:**

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/cloud/remove
```

**Headers:** sessionid

**Body:**

```json
[
    "<CLOUD_ID>",
    "<CLOUD_ID>" 
]
```