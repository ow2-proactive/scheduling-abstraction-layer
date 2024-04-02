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

def getKubeToken(){
    def soutToken = new StringBuilder(), serrToken = new StringBuilder()
    // def proc = "kubeadm token create \"$(kubeadm token generate)\" --print-join-command --ttl=1h >  /tmp/join_call.txt".execute()
    def procToken = "kubeadm token generate".execute()
    procToken.consumeProcessOutput(soutToken, serrToken)
    procToken.waitForOrKill(1000)
    println "out> ${soutToken}\nerr> ${serrToken}"

    def soutCommand = new StringBuilder(), serrCommand = new StringBuilder()
    procCommand="kubeadm token create ${soutToken} --print-join-command --ttl=1h".execute()
    procCommand.consumeProcessOutput(soutCommand, serrCommand)
    procCommand.waitForOrKill(1000)
    println "out> ${soutCommand}\nerr> ${serrCommand}"
    variables.put("kubeCommand", soutCommand)
}

getKubeToken()

// Call the function to check the IP
result = checkIPFromServers()