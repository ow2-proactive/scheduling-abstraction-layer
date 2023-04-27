//Collect public & private IP addresses script
def requestedPortName = variables.get("requestedPortName")
if (requestedPortName != null) {
    def publicRequestedPort
    def privateRequestedPort
    def count = 0
    def countPrv = 0

    variables.each { key, value ->
        if (key.contains(requestedPortName) && !key.contains("prv")) {
            if (count == 0) {
                publicRequestedPort = value.toString()
                count++
            } else {
                publicRequestedPort += "," + value.toString()
                count++
            }
        } else if (key.contains(requestedPortName)) {
            if (countPrv == 0) {
                privateRequestedPort = value.toString()
                countPrv++
            } else {
                privateRequestedPort += "," + value.toString()
                countPrv++
            }
        }
    }

    println "publicRequestedPort: " + publicRequestedPort
    variables.put(requestedPortName, publicRequestedPort)

    println "privateRequestedPort: " + privateRequestedPort
    variables.put(requestedPortName + "_prv", privateRequestedPort)
}