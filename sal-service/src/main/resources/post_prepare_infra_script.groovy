//Post prepare infrastructure script
def providedPortName = variables.get("providedPortName")
def providedPortValue = variables.get("providedPortValue")

if (providedPortName?.trim()){
    def ipAddr = new File(providedPortName+"_ip").text.trim()
    def publicProvidedPort = ipAddr + ":" + providedPortValue
    variables.put(providedPortName + variables.get("PA_TASK_ID"), publicProvidedPort)
    println("Provided variable " + providedPortName + "=" + publicProvidedPort)
}