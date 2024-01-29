#### 2.1- Add clouds endpoint:

**Description**: Add clouds to SAL and update node candidates asynchronously according to cloud params

**Path:**

```url
POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/clouds
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
            "keyPairName": null,
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

**Returns**: 0 if clouds has been added properly. A greater than 0 value otherwise

#### 2.2- Is any fetching/cleaning node candidates process running endpoint:

**Description**: Verify if there is any asynchronous fetching/cleaning node candidates process in progress

**Path:**

```url
GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/clouds/async
```

**Headers:** sessionid

**Body:** None

**Returns**: true if at least one asynchronous node candidates process is in progress, false otherwise

#### 2.3- Get all clouds endpoint:

**Description**: Get all registered clouds

**Path:**

```url
GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/clouds
```

**Headers:** sessionid

**Body:** None

**Returns**: List of registered clouds (AWS use case):

```json
[
 {
        "cloudId": "nebulous-aws-sal-1",
        "nodeSourceNamePrefix": "aws-ec2nebulous-aws-sal-1",
        "cloudProviderName": "aws-ec2",
        "cloudType": "PUBLIC",
        "subnet": null,
        "securityGroup": null,
        "sshCredentials": {
            "username": null,
            "keyPairName": null,
            "privateKey": null
        },
        "endpoint": null,
        "scopePrefix": null,
        "scopeValue": null,
        "identityVersion": null,
        "dummyInfrastructureName": "<dummy infrastructure name>",
        "defaultNetwork": null,
        "blacklist": "",
        "deployedRegions": {},
        "deployedWhiteListedRegions": {},
        "credentials": {
            "credentialsId": "<AWS_CREDS_ID>",
            "userName": "<AWS_USER>",
            "password": null,
            "privateKey": "<AWS_SECRET>",
            "publicKey": null,
            "domain": null
        },
        "deploymentNodeNames": []
    }
]
```

#### 2.4- Get cloud images endpoint:

**Description**: Get the list of all images of all registered clouds or related to a specified one

**Path:**

```url
GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/clouds/images?<CLOUD_ID>
```

**Path Variable:** cloudid

**Headers:** sessionid

**Body:** None

**Returns**: List of images following this format (AWS use case):

```json
[
    {
        "id": "nebulous-aws-sal-1/eu-west-1/ami-09ddc592784e0c759",
        "name": "PrestoCloud-Test-AWS-image",
        "providerId": "ami-09ddc592784e0c759",
        "operatingSystem": {
            "operatingSystemFamily": "UNKNOWN_OS_FAMILY",
            "operatingSystemArchitecture": "I386",
            "operatingSystemVersion": 0.00
        },
        "location": {
            "id": "nebulous-aws-sal-1/eu-west-1",
            "name": "eu-west-1",
            "providerId": "eu-west-1",
            "locationScope": "REGION",
            "isAssignable": true,
            "geoLocation": {
                "city": "Ireland",
                "country": "Ireland",
                "latitude": 53.3331,
                "longitude": -6.2489
            },
            "parent": null,
            "state": null,
            "owner": null
        },
        "state": null,
        "owner": null
    }
]
```

#### 2.5- Get cloud locations endpoint:

**Description**: Get the list of all locations of all registered clouds or related to a specified one

**Path:**

```url
GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/clouds/locations?<CLOUD_ID>
```

**Path Variable:** cloudid

**Headers:** sessionid

**Body:** None

**Returns**: List of locations following this format (AWS use case):

```json
[
    {
            "id": "nebulous-aws-sal-1/eu-west-1",
            "name": "eu-west-1",
            "providerId": "eu-west-1",
            "locationScope": "REGION",
            "isAssignable": true,
            "geoLocation": {
                "city": "Ireland",
                "country": "Ireland",
                "latitude": 53.3331,
                "longitude": -6.2489
            },
            "parent": null,
            "state": null,
            "owner": null
    }
]
```

#### 2.6- Get cloud hardware endpoint:

**Description**: Get the list of all hardware of all registered clouds or related to a specified one

**Path:**

```url
GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/clouds/hardware?<CLOUD_ID>
```

**Path Variable:** cloudid

**Headers:** sessionid

**Body:** None

**Returns**: List of hardware following this format (AWS use case):

```json
[
    {
            "id": "nebulous-aws-sal-1/eu-west-1/a1.2xlarge",
            "name": "a1.2xlarge",
            "providerId": "a1.2xlarge",
            "cores": 8,
            "ram": 16384,
            "disk": 8.0,
            "fpga": 0,
            "location": {
                "id": "nebulous-aws-sal-1/eu-west-1",
                "name": "eu-west-1",
                "providerId": "eu-west-1",
                "locationScope": "REGION",
                "isAssignable": true,
                "geoLocation": {
                    "city": "Ireland",
                    "country": "Ireland",
                    "latitude": 53.3331,
                    "longitude": -6.2489
                },
                "parent": null,
                "state": null,
                "owner": null
            },
            "state": null,
            "owner": null
    }
]
```

#### 2.7- Remove clouds endpoint:

**Description**: Remove a list of defined clouds

**Path:**

```url
DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/clouds/remove
```

**Headers:** sessionid

**Body:**

```json
[
    "<CLOUD_ID>",
    "<CLOUD_ID>"
]
```

**Returns**: true if all the clouds from the list are removed
