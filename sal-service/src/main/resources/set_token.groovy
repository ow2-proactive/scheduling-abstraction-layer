def nodeURL = variables.get("nodeURL")
def nodeToken = variables.get("nodeToken")
rmapi.connect()
println nodeURL
rmapi.addNodeToken(nodeURL, nodeToken)
rmapi.disconnect()