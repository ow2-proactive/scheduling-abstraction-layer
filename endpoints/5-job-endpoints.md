#### 5.1- CreateJob endpoint:

**Description**: Create a ProActive job skeleton

**Path**:

```url
🟡 POST  {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job
```

**Headers:** sessionid

**Reply: True if the job is added, False if the job already exists.**

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

#### 5.2- GetJobs endpoint:

**Description**: Get all job skeletons

**Path**:

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job
```

**Headers:** sessionid

**Returns**: A JSON list of the jobs.

#### 5.3- GetJob endpoint:

**Description**: Get a Job skeleton using the job identifier

**Path**:

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job/<JOB_ID>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Returns**: A JSON of the job.

#### 5.4- StopJobs endpoint:

**Description**: Stop all the jobs that are submitted to ProActive.

**Path**:

```url
🔵 PUT {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job/stop?jobIds=[<JOB_ID>]
```

**Path Variable:** A list of job identifiers `jobIds`

**Headers:** sessionid

#### 5.5- SubmitJob endpoint:

**Description**: Submit a job constructed in lazy-mode to the ProActive Scheduler.

**Path**:

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job/<JOB_ID>/submit
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Returns**: Submitted job id if successful, otherwise -1.

#### 5.6- GetJobState endpoint:

**Description**: Get the ProActive job state

**Path**:

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job/<JOB_ID>/status
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Returns**: the status of the job.

#### 5.7- WaitForJob endpoint:

**Description**: Wait for execution and get results of a job.

**Path**:

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job/<JOB_ID>/wait?timeout=<TIME_IN_SEC>
```

**Path Variable:** The job identifier `<JOB_ID>`

**Request Parameter:** `timeout` parameter that describes the time to wait before declaring a timeout.

**Headers:** sessionid

**Returns**: Job result.

#### 5.8- killJob endpoint:

**Description**: To kill a job running on the ProActive scheduler.

**Path**:

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job/<JOB_ID>/kill
```

**Path Variable:** The job identifier `<JOB_ID>`

**Headers:** sessionid

**Returns**: True if the job was successfully killed, false otherwise.

#### 5.9- WaitForTask endpoint:

**Description**: Wait for execution and get results of a task

**Path**:

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job/<JOB_ID>/<TASK_ID>/wait?timeout=<TIME_IN_SEC>
```

**Path Variable:** The job identifier `<JOB_ID>` and the task identifier `<TASK_ID>`

**Request Parameter:** `timeout` parameter that describes the time to wait before declaring a timeout.

**Headers:** sessionid

**Returns**: Task result.

#### 5.10- GetTaskResult endpoint:

**Description**: Get a task result.

**Path**:

```url
🟢 GET {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job/<JOB_ID>/<TASK_ID>/result
```

**Path Variable:** The job identifier `<JOB_ID>` and the task identifier `<TASK_ID>`

**Headers:** sessionid

**Returns**: Task result.

#### 5.11- KillAllActivePAJobs endpoint:

**Description**: Kill all active jobs in ProActive Scheduler

**Path**:

```url
🔵 PUT {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job/kill
```

**Headers:** sessionid

**Returns**: True if the killing of all the jobs was successful, false otherwise.

#### 5.12- RemoveAllPAJobs endpoint:

**Description**: Remove all jobs from the ProActive Scheduler

**Path**:

```url
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/job/remove
```

**Headers:** sessionid

**Returns**: True if the removing of all the jobs was successful, false otherwise.
