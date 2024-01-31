#### 5.1- Create job endpoint:

**Description**: Create a SAL job

**Path**:

```url
POST  {{protocol}}://{{sal_host}}:{{sal_port}}/sal/jobs
```

**Headers**: sessionid

**Returns**: True if the job is added, False if the job already exists

**Body**:  A JSON body following this format:

```json
{
    "communications": [{
            "portProvided": "ComponentDBPort",
            "portRequired": "ComponentPortDbReq"
        }, {
            "portProvided": "ComponentAppPort",
            "portRequired": "ComponentPortAppReq"
        }
    ],
    "jobInformation": {
        "id": "FCRnewLight",
        "name": "FCRLight_Deployment_JOB"
    },
    "tasks": [{
            "name": "Component_App",
            "installation": {
                    "preInstall": "echo \"Hello from Component_App preInstall script\"",
                    "install": null,
                    "postInstall": "echo \"Hello from Component_App postInstall script\"",
                    "start": "echo \"Hello from Component_App Start script\"",
                    "stop": null,
                    "update": null,
                    "startDetection": null,
                    "type": "commands",
                    "operatingSystem": {
                        "operatingSystemFamily": "UBUNTU",
                        "operatingSystemVersion": 2004
                    }
                },
            "ports": [{
                    "type": "PortProvided",
                    "name": "ComponentAppPort",
                    "port": 8087
                }
            ]
        }, {
            "name": "Component_LB",
            "installation": {
                    "preInstall": "echo \"Hello from Component_LB preInstall script\"",
                    "install": "echo \"Hello from Component_LB Install script\"",
                    "postInstall": "echo \"Hello from Component_LB postInstall script\"",
                    "start": "echo \"Hello from Component_LB Start script\"",
                    "stop": null,
                    "update": "echo \"Hello from Component_LB Update script\"",
                    "startDetection": null,
                    "type": "commands",
                    "operatingSystem": {
                        "operatingSystemFamily": "UBUNTU",
                        "operatingSystemVersion": 2004
                    }
                },
            "ports": [{
                    "type": "PortProvided",
                    "name": "ComponentLBPort",
                    "port": 8087
                }, {
                    "type": "PortRequired",
                    "name": "ComponentPortAppReq",
                    "isMandatory": false
                }, {
                    "type": "PortRequired",
                    "name": "ComponentPortDbReq",
                    "isMandatory": true
                }
            ]
        }, {
            "name": "Component_DB",
            "installation": {
                    "preInstall": "echo \"Hello from Component_DB preInstall script\"",
                    "install": "echo \"Hello from Component_DB Install script\"",
                    "postInstall": "echo \"Hello from Component_DB postInstall script\"",
                    "start": "echo \"Hello from Component_DB Start script\"",
                    "stop": null,
                    "update": null,
                    "startDetection": null,
                    "type": "commands",
                    "operatingSystem": {
                        "operatingSystemFamily": "UBUNTU",
                        "operatingSystemVersion": 2004
                    }
                },
            "ports": [{
                    "type": "PortProvided",
                    "name": "ComponentDBPort",
                    "port": 3306
                }
            ]
        }
    ]
}
```

Also a job can define a Docker container:

```json
{
    "communications": [{
            "portProvided": "ComponentMySqlPort",
            "portRequired": "DBRequiredPort"
        }
    ],
    "jobInformation": {
        "name": "FCDocker_JOB",
        "id": "ExampleWithDocker"
    },
    "tasks": [{
            "installation": {
                    "environmentVars": {
                        "MYSQL_DATABASE": "meldb",
                        "MYSQL_USER": "melodic",
                        "MYSQL_PASSWORD": "testpwd",
                        "MYSQL_ROOT_PASSWORD": "admin"
                    },
                    "port": "3306:3306",
                    "dockerImage": "mariadb:10.8.2",
                    "type": "docker"
                },
            "name": "Component_MySql",
            "ports": [{
                    "port": 3306,
                    "name": "ComponentMySqlPort",
                    "type": "PortProvided"
                }
            ]
        }, {
            "installation": {
                    "environmentVars": {
                        "WORDPRESS_DB_USER": "melodic",
                        "WORDPRESS_DB_PASSWORD": "testpwd",
                        "WORDPRESS_DB_HOST": "$PUBLIC_DBRequiredPort",
                        "WORDPRESS_DB_NAME": "meldb"
                    },
                    "port": "80:80",
                    "dockerImage": "wordpress",
                    "type": "docker"
                },
            "name": "Component_Wordpress",
            "ports": [{
                    "port": 80,
                    "name": "WrodpressProvidedPort",
                    "type": "PortProvided"
                }, {
                    "name": "DBRequiredPort",
                    "type": "PortRequired",
                    "isMandatory": false
                }
            ]
        }
    ]
}
```

#### 5.2- Get all jobs endpoint:

**Description**: Get all SAL jobs

**Path**:

```url
GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/jobs
```

**Headers:** sessionid

**Returns**: A JSON list of the jobs. An example:

```json
[
    {
        "jobId": "FCRnewLight2",
        "name": "FCRLight_Deployment_JOB",
        "variables": {},
        "submittedJobId": 18,
        "submittedJobType": "FIRST_DEPLOYMENT",
        "tasks": [
            {
                "taskId": "FCRnewLight2Component_App",
                "name": "Component_App",
                "type": "commands",
                "installation": {
                    "type": "commands",
                    "preInstall": "echo \"Hello from Component_App preInstall script\"",
                    "install": null,
                    "postInstall": "echo \"Hello from Component_App postInstall script\"",
                    "preStart": null,
                    "start": "echo \"Hello from Component_App Start script\"",
                    "postStart": null,
                    "preStop": null,
                    "stop": null,
                    "postStop": null,
                    "update": null,
                    "startDetection": null,
                    "operatingSystem": {
                        "operatingSystemFamily": "UBUNTU",
                        "operatingSystemVersion": 2004.0
                    },
                    "type": "commands"
                },
                "environment": {
                    "type": "docker",
                    "dockerImage": null,
                    "port": null,
                    "environmentVars": {},
                    "type": "docker"
                },
                "portsToOpen": [
                    {
                        "value": 8087
                    }
                ],
                "parentTasks": {},
                "submittedTaskNames": [
                    "acquireAWSNode_Component_App_0",
                    "prepareInfra_Component_App_0",
                    "Component_App_install_0",
                    "Component_App_start_0"
                ],
                "deploymentFirstSubmittedTaskName": "acquireAWSNode_Component_App",
                "deploymentLastSubmittedTaskName": "Component_App_start",
                "nextDeploymentID": 1,
                "deploymentNodeNames": [
                    "component-App-1-0"
                ]
            },
            {
                "taskId": "FCRnewLight2Component_DB",
                "name": "Component_DB",
                "type": "commands",
                "installation": {
                    "type": "commands",
                    "preInstall": "echo \"Hello from Component_DB preInstall script\"",
                    "install": "echo \"Hello from Component_DB Install script\"",
                    "postInstall": "echo \"Hello from Component_DB postInstall script\"",
                    "preStart": null,
                    "start": "echo \"Hello from Component_DB Start script\"",
                    "postStart": null,
                    "preStop": null,
                    "stop": null,
                    "postStop": null,
                    "update": null,
                    "startDetection": null,
                    "operatingSystem": {
                        "operatingSystemFamily": "UBUNTU",
                        "operatingSystemVersion": 2004.0
                    },
                    "type": "commands"
                },
                "environment": {
                    "type": "docker",
                    "dockerImage": null,
                    "port": null,
                    "environmentVars": {},
                    "type": "docker"
                },
                "portsToOpen": [
                    {
                        "value": 3306
                    }
                ],
                "parentTasks": {},
                "submittedTaskNames": [],
                "deploymentFirstSubmittedTaskName": null,
                "deploymentLastSubmittedTaskName": null,
                "nextDeploymentID": 0,
                "deploymentNodeNames": []
            },
            {
                "taskId": "FCRnewLight2Component_LB",
                "name": "Component_LB",
                "type": "commands",
                "installation": {
                    "type": "commands",
                    "preInstall": "echo \"Hello from Component_LB preInstall script\"",
                    "install": "echo \"Hello from Component_LB Install script\"",
                    "postInstall": "echo \"Hello from Component_LB postInstall script\"",
                    "preStart": null,
                    "start": "echo \"Hello from Component_LB Start script\"",
                    "postStart": null,
                    "preStop": null,
                    "stop": null,
                    "postStop": null,
                    "update": "echo \"Hello from Component_LB Update script\"",
                    "startDetection": null,
                    "operatingSystem": {
                        "operatingSystemFamily": "UBUNTU",
                        "operatingSystemVersion": 2004.0
                    },
                    "type": "commands"
                },
                "environment": {
                    "type": "docker",
                    "dockerImage": null,
                    "port": null,
                    "environmentVars": {},
                    "type": "docker"
                },
                "portsToOpen": [
                    {
                        "value": 8087
                    }
                ],
                "parentTasks": {
                    "ComponentPortDbReq": "Component_DB",
                    "ComponentPortAppReq": "Component_App"
                },
                "submittedTaskNames": [
                    "acquireAWSNode_Component_LB_0",
                    "prepareInfra_Component_LB_0",
                    "Component_LB_install_0",
                    "Component_LB_start_0"
                ],
                "deploymentFirstSubmittedTaskName": "acquireAWSNode_Component_LB",
                "deploymentLastSubmittedTaskName": "Component_LB_start",
                "nextDeploymentID": 1,
                "deploymentNodeNames": [
                    "component-LB-1-0"
                ]
            }
        ],
        "communications": {
            "ComponentDBPort": "ComponentPortDbReq",
            "ComponentAppPort": "ComponentPortAppReq"
        },
        "sinkTasks": [
            "FCRnewLight2Component_LB"
        ],
        "rootTasks": [
            "FCRnewLight2Component_DB",
            "FCRnewLight2Component_App"
        ]
    }
]
```

#### 5.3- Get job endpoint:

**Description**: Get a SAL Job

**Path**:

```url
GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/jobs/<JOB_ID>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Returns**: A JSON of the job.

#### 5.4- Submit job endpoint:

**Description**: Submit a job constructed in lazy-mode to the ProActive Scheduler.

**Path**:

```url
POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/jobs/<JOB_ID>/submit
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Returns**: Submitted job id if successful, otherwise -1.

#### 5.5- Get job status endpoint:

**Description**: Get a SAL job status

**Path**:

```url
GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/jobs/<JOB_ID>/status
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Returns**: the status of the job. An example:

```json
{
    "submittedJobType": "FIRST_DEPLOYMENT",
    "jobStatus": "FINISHED"
}
```

#### 5.6- Kill job endpoint:

**Description**: Kill a job running on the ProActive scheduler

**Path**:

```url
DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/jobs/<JOB_ID>/kill
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Returns**: True if the job was successfully killed, false otherwise

#### 5.7- Remove all PA jobs endpoint:

**Description**: Remove all jobs from the ProActive Scheduler

**Path**:

```url
DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/jobs/remove
```

**Headers:** sessionid

**Returns**: True if the jobs were successfully removed, false otherwise

#### 5.13- Remove job endpoint:

**Description**: Remove a specified job

**Path**:

```url
DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/jobs/remove/<JOB_ID>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Returns**: True if the job was successfully removed, false otherwise
