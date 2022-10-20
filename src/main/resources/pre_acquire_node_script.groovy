// Connecting to the ResourceManager API
rmapi.connect()

// Getting NS configuration settings
def retCode = 0
def nodeSourceName = variables.get("NS_name")
def flag = false
def count = 0

while (!flag) {
    try {
        if (rmapi.getNodeSourcePingFrequency(nodeSourceName)) {
            println " Node source ready !"
        }
        flag = true
    } catch (Exception e) {
        if (count > 25) {
            println "NodeSource is still not reachable after " + count + " seconds. Aborting."
            System.exit(1)
        }
        println "NodeSource is not yet reachable, time spent: " + count
        sleep(5000)
        count += 5
    }
}

// Disconnecting from the ResourceManager API
rmapi.disconnect()