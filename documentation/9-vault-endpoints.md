#### 9.1- registerNewSecrets endpoint:

**Description**: Register new secrets in ProActive vault For more details check: [VaultKey class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-common/src/main/java/org/ow2/proactive/sal/model/VaultKey.java), [VaultService class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/service/VaultService.java), and [VaultRest class](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/sal-service/src/main/java/org/ow2/proactive/sal/service/rest/VaultRest.java). **Path**:

```text
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/vault
```

**Headers:** sessionid

**Body:**

A JSON body of the key/secret pairs:

```json
{
"Key1":"Secret1",
"Key2":"Secret2",
"Key3":"Secret3"
}
```

**Returns**: True if the secrets were successfully added, false otherwise.

#### 9.2- removeSecret endpoint:

**Description**: Remove a secret from the ProActive vault

**Path**:

```text
🔴 DEL {{protocol}}://{{sal_host}}:{{sal_port}}/sal/vault
```

**Headers:** sessionid

**Returns**: True if the secrets were successfully removed, false otherwise.