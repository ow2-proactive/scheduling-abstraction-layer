// Conncting to the Scheduler
println "[+] Preparation of Nodes ... "
print "(1/4) Connecting to the RM ..."
rmapi.connect()
println " ... OK !"

// Getting NS configuration settings
def retCode = 0;
def nodeSourceName = variables.get("NS_name")
def nodeToken = variables.get("token")
def hostName = variables.get("host_name")

// Enforcing ....
print "(2/4) Searching for nodes ..."
def nodeURLs = rmapi.getRMStateFull().getNodesEvents().findAll { nodeEvent ->
    return (nodeSourceName == nodeEvent.getNodeSource() && hostName == nodeEvent.getHostName());
}.collect { nodeEvent ->
    return (nodeEvent.getNodeUrl());
}
println " ... OK !"
println "Found node URLs : " + nodeURLs.toString()

print "(3/4) Acquiring nodes ..."
nodeURLs.each { nodeURL ->
    rmapi.addNodeToken(nodeURL, nodeToken)
}
println " ... OK !"

print "(4/4) Logging out ..."
rmapi.disconnect();
println " ... OK !"
return retCode;