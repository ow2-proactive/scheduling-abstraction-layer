// Conncting to the Scheduler
println "[+] Preparation of Nodes ... "
print "(1/3) Connecting to the RM ..."
rmapi.connect()
println " ... OK !"

// Getting NS configuration settings
def retCode = 0;
def nodeSourceName = variables.get("NS_name")
def nVMs = variables.get("nVMs").toInteger()
def synchronous = variables.get("synchronous").toBoolean()
def timeout = Long.valueOf(variables.get("timeout"))
def nodeConfigJson = variables.get("nodeConfigJson")
def nodeToken = variables.get("token")

// Enforcing ....
print "(2/3) Acquiring nodes ..."
def nodeURLs = rmapi.acquireNodes(nodeSourceName,
        nVMs,
        synchronous,
        timeout,
        nodeConfigJson)

nodeURLs.each { nodeURL ->
    variables.put("nodeURL", nodeURL)
    resultMap.put("nodeURL", nodeURL)
    rmapi.addNodeToken(nodeURL, nodeToken)
}
println " ... OK !"
print "(3/3) Logging out ..."
rmapi.disconnect();
println " ... OK !"
return retCode;