import java.util.Collections

// Conncting to the Scheduler
println "[+] Remove node ... "
print "(1/3) Connecting to the RM ..."
rmapi.connect()
println " ... OK !"

// Getting configuration settings
def retCode = 0;
def nodeName = variables.get("nodeName")
def preempt = variables.get("preempt").toBoolean()

// Enforcing ....
println "(2/3) Removing nodes ..."
def nodeURLs = rmapi.searchNodes(Collections.singletonList(nodeName), true);
nodeURLs.each { nodeURL ->
    rmapi.removeNode(nodeURL, preempt)
    println "   Node " + nodeName + " with URL: " + nodeURL + " has been removed successfully."
}

println " ... OK !"
print "(3/3) Logging out ..."
rmapi.disconnect();
println " ... OK !"
return retCode;