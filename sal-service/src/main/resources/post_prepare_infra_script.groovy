//Post prepare infrastructure script
import groovy.json.JsonSlurperClassic

def componentName = variables.get("ComponentName")
def ipAddr = new File(componentName+"_ip").text.trim()
def prvIpAddr = new File(componentName+"_prv_ip").text.trim()
def jobId = variables.get("PA_JOB_ID")
def providedPorts = new JsonSlurperClassic().parseText( variables.get("providedPorts") )

providedPorts.each { providedPort ->
    if (providedPort["requiringComponentName"]?.trim()) {
        synchronizationapi.merge(jobId + providedPort["requiringComponentName"],
                "PUBLIC_" + providedPort["requiringPortName"],
                ipAddr + ":" + providedPort["portValue"],
                "{k, x -> x.concat(\"," + ipAddr + ":" + providedPort["portValue"] + "\")}")
        println("Component [" + componentName + "] providing: PUBLIC_" + providedPort["requiringPortName"] + "=" + ipAddr + ":" + providedPort["portValue"] + " to [" + providedPort["requiringComponentName"] + "]")
        synchronizationapi.merge(jobId + providedPort["requiringComponentName"],
                "PRIVATE_" + providedPort["requiringPortName"],
                prvIpAddr + ":" + providedPort["portValue"],
                "{k, x -> x.concat(\"," + prvIpAddr + ":" + providedPort["portValue"] + "\")}")
        println("Component [" + componentName + "] providing: PRIVATE_" + providedPort["requiringPortName"] + "=" + prvIpAddr + ":" + providedPort["portValue"] + " to [" + providedPort["requiringComponentName"] + "]")
    } else {
        println ("Component [" + componentName + "] providing: " + providedPort["requiringPortName"] + "=" + ipAddr + ":" + providedPort["portValue"])
    }
}