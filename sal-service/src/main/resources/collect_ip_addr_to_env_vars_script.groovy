//Collect public & private IP addresses script
def jobId = variables.get("PA_JOB_ID")
def componentName = variables.get("ComponentName")

def outputFileName = 'envVars.sh'
def writer = new File(outputFileName).newWriter()
writer.writeLine "#!/bin/bash\n"
for (Map.Entry mapping : synchronizationapi.entrySet(jobId + componentName)) {
    writer.writeLine "echo \"Exporting missing [" + mapping.getKey() + "] env var with value: " + mapping.getValue() + "\n\""
    writer.writeLine "export " + mapping.getKey() + "=" + mapping.getValue() + "\n"
}
writer.flush()
writer.close()

'chmod u+x envVars.sh'.execute().text