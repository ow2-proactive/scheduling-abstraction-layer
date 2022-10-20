URL url_name = new URL("https://api.ipify.org");
BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));
// reads system IPAddress
systemipaddress = sc.readLine().trim();
System.out.println("Public IP Address: " + systemipaddress + "\n");

result = systemipaddress