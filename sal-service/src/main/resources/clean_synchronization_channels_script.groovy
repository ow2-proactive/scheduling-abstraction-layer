def jobId = variables.get("PA_JOB_ID")

println "Cleaning synchronization channels for job: " + jobId
variables.each { key, value ->
    if (key.startsWith("ComponentName")) {
        synchronizationapi.deleteChannel(jobId + value)
        println "Channel " + jobId + value + " deleted."
    }
}
println "Cleaning synchronization channels done!"