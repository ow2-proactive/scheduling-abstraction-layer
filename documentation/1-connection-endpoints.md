#### 1.1- Connect endpoint:

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/pagateway/connect
```

**Headers:** None

**Body:**

*   &quot;username&quot;: Proactive server username.
*   &quot;password&quot;: Proactive server password.

**Returns:** A text format reply of the session ID.

#### 1.2- Disconnect endpoint:

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal
```

**Headers:**

*   sessionid

**Body:**

**Returns: None**
