//Collect public IP addresses script
def requestedPortName = variables.get("requestedPortName")
if (requestedPortName != null) {
    def publicRequestedPort
    def count = 0

    variables.each { key, value ->
        if (key.contains(requestedPortName)) {
            if (count == 0) {
                publicRequestedPort = value.toString()
                count++
            } else {
                publicRequestedPort += "," + value.toString()
                count++
            }
        }
    }

    println "publicRequestedPort: " + publicRequestedPort
    variables.put(requestedPortName, publicRequestedPort)
}