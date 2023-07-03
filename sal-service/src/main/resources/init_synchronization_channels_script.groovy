def jobId = variables.get("PA_JOB_ID")

println "Initialising synchronization channels for job: " + jobId
variables.each { key, value ->
    if (key.startsWith("ComponentName")) {
        synchronizationapi.createChannel(jobId + value, false)
        println "Channel " + jobId + value + " created."
    }
}
println "Initialising synchronization channels done!"