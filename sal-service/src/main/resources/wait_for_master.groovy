import groovy.transform.Synchronized

// Define a function to retrieve the IP from a server
def retrieveIPFromServer(server) {
    try {
        def url = "http://${server}"
        def response = new URL(url).text
        return response.trim()
    } catch (Exception e) {
        println "Failed to retrieve IP from ${server}: ${e.message}"
        return null
    }
}

// Define a function to check the IP from multiple servers
@Synchronized
def checkIPFromServers() {
    def servers = [
            "checkip.amazonaws.com",
            "api.ipify.org",
            "ifconfig.me",
            "ipinfo.io/ip",
            "icanhazip.com",
            "ident.me",
            "myip.dnsomatic.com"
    ];
    for (def server : servers) {
        def ip = retrieveIPFromServer(server)
        if (ip) {
            println "Address was retrieved from ${server}"
            println "Public IP: ${ip}"
            variables.put("masterIp", ip)
            return ip
        }
    }

    println "Unable to retrieve the public IP from any server."
}

def getKubeToken(int retryCount = 5, int retryDelay = 1000) {
    for (int attempt = 1; attempt <= retryCount; attempt++) {
        println "Attempt ${attempt} of ${retryCount}"

        // Generate token
        def soutToken = new StringBuilder(), serrToken = new StringBuilder()
        def procToken = "kubeadm token generate".execute()
        procToken.consumeProcessOutput(soutToken, serrToken)
        procToken.waitForOrKill(1000)
        println "Token generation - out> ${soutToken}\nerr> ${serrToken}"

        if (serrToken.toString().trim()) {
            println "Error generating token: ${serrToken}"
            sleep(retryDelay)
            continue
        }

        def token = soutToken.toString().trim()

        // Create join command
        def soutCommand = new StringBuilder(), serrCommand = new StringBuilder()
        def procCommand = "kubeadm token create ${token} --print-join-command --ttl=1h".execute()
        procCommand.consumeProcessOutput(soutCommand, serrCommand)
        procCommand.waitForOrKill(1000)
        println "Join command generation - out> ${soutCommand}\nerr> ${serrCommand}"

        if (serrCommand.toString().trim() || soutCommand.toString().trim().isEmpty()) {
            println "Error generating join command: ${serrCommand}"
            sleep(retryDelay)
            continue
        }

        // Store the join command and return
        def joinCommand = soutCommand.toString().trim()
        variables.put("kubeCommand", joinCommand)
        println "kubeCommand stored: ${joinCommand}"
        return
    }

    println "Failed to generate kube join command after ${retryCount} attempts."
}

// Call the function with the desired number of retries
getKubeToken(5, 1000)  // Retry 3 times with a delay of 1000ms (1 second) between attempts


// Call the function to check the IP
result = checkIPFromServers()