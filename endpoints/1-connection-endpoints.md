#### 1.1- Connect endpoint:
**Description**: Establishing the connection to ProActive server. Use your ProActive username ({{myLogin}}) and {{myPassword}} to establish connection.

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/pagateway/connect
```

**Headers:** None

**Body:**

*   &quot;username&quot;: {{myLogin}}
*   &quot;password&quot;: {{myPassword}}

**Reply:** A text format reply of the session ID.

_NOTE:_ session ID which is returned is used as Header for all other endpoints.
In a case that HTTP 500 error is returned with body saying that NonConnectedException occur, it means that connection to ProActive server was lost and is needed to call Connect endpoint again and use the new session ID.

#### 1.2- Disconnect endpoint:
**Description**: Disconnecting from ProActive server.

**Path:**

```url
🟡 POST {{protocol}}://{{sal_host}}:{{sal_port}}/sal/pagateway/disconnect
```

**Headers:** `sessionid`

**Body:** None

**Reply:** None
