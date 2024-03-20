def nodeURL = variables.get("nodeURL")
rmapi.connect()
println nodeURL
rmapi.removeNode(nodeURL,false)
rmapi.disconnect()