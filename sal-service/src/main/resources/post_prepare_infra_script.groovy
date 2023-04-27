//Post prepare infrastructure script
def providedPortName = variables.get("providedPortName")
def providedPortValue = variables.get("providedPortValue")

if (providedPortName?.trim()){
    def ipAddr = new File(providedPortName+"_ip").text.trim()
    def prvIpAddr = new File(providedPortName+"_prv_ip").text.trim()
    def publicProvidedPort = ipAddr + ":" + providedPortValue
    def privateProvidedPort = prvIpAddr + ":" + providedPortValue
    variables.put(providedPortName + variables.get("PA_TASK_ID"), publicProvidedPort)
    println("Provided variable " + providedPortName + "=" + publicProvidedPort)
    variables.put(providedPortName + "_prv_" + variables.get("PA_TASK_ID"), privateProvidedPort)
    println("Provided variable " + providedPortName + "_prv=" + privateProvidedPort)
}