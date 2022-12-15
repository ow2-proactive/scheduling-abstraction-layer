import javax.net.ssl.*
import java.security.cert.X509Certificate

// Calculing privatekey fingerprint
println "== Baguette request preparation"
println "-- Getting parameters"
def password = credentials.containsKey('target_password') ? credentials.get('target_password') : ""
File privateKeyFile = new File("/tmp/ems-keypair")
File ipFile = new File("/tmp/ip.txt")
if (!privateKeyFile.exists()) {
    println "ERR: PrivateKey file doesn't exist. Exiting"
    return 255
}
if (!ipFile.exists()) {
    println "ERR: Public ip file doesn't exist. Exiting"
    return 255
}
def privateKey = privateKeyFile.text
def ip = ipFile.text

def fingerprint = '' // TODO:

println "-- Validating parameters"
// Chacking parameters values
def baguetteBearer = variables.get('authorization_bearer');
def baguetteIp = variables.get('baguette_ip');
def baguettePort = variables.get('baguette_port');
def usingHttps = variables.get("using_https").toBoolean()
def emsUrl = String.format("%s://%s:%s/baguette/registerNode", usingHttps ? "https" : "http",baguetteIp,baguettePort)
def osName = variables.get("target_os_name")
def osFamily = variables.get("target_os_family")
def osVersion = variables.get("target_os_version")
def osArch = variables.get("target_os_arch")
//def ip = variables.get("target_ip")
def hdwCores = variables.get("target_hdw_cores")
def hdwMemory = variables.get("target_hdw_memory")
def hdwDisk = variables.get("target_hdw_disk")
def port = 22 //variables.get("target_port")
def username = "whoami".execute().text[0..-2] // We capture the output of whoami command & remove the \n char
def type = variables.get("target_type")
def name = variables.get("target_name")
def provider = variables.get("target_provider")
def openPorts = variables.get("target_open_ports")
def imageId = variables.get("target_image_id")
def region = variables.get("region")
def locationCountry = variables.get("location_country")
def locationCity = variables.get("location_city")
def locationLongitude = variables.get("location_longitude")
def locationLatitude = variables.get("location_latitude")
def id = variables.get("id")

// Request preparation
def isInputValid = true;
isInputValid &= (baguetteBearer != null);
isInputValid &= (baguetteIp != null);
isInputValid &= (baguettePort != null);
isInputValid &= (emsUrl != null);
isInputValid &= (osName != null);
isInputValid &= (osFamily != null);
isInputValid &= (osVersion != null);
isInputValid &= (osArch != null);
isInputValid &= (ip != null);
isInputValid &= (hdwCores != null);
isInputValid &= (hdwMemory != null);
isInputValid &= (hdwDisk != null);
isInputValid &= (port != null);
isInputValid &= (username != null);
isInputValid &= (type != null);
isInputValid &= (name != null);
isInputValid &= (provider != null);
isInputValid &= (imageId != null);
isInputValid &= (region != null);
isInputValid &= (locationCountry != null);
isInputValid &= (locationCity != null);
isInputValid &= (locationLongitude != null);
isInputValid &= (locationLatitude != null);

if (!isInputValid) {
    println "ERR: One or many provided parameters are invalid."
    return 255;
}

println "-- payload preparation"
def requestPayload = [:]
requestPayload.operatingSystem = [:];
requestPayload.operatingSystem.name = osName;
requestPayload.operatingSystem.family = osFamily;
requestPayload.operatingSystem.version = osVersion;
requestPayload.operatingSystem.arch = osArch;
requestPayload.address = ip;
requestPayload.hardware = [:];
requestPayload.hardware.cores = hdwCores;
requestPayload.hardware.memory = hdwMemory;
requestPayload.hardware.disk = hdwDisk;
requestPayload.ssh = [:];
requestPayload.ssh.port = port;
requestPayload.ssh.username = username;
requestPayload.type = type;
requestPayload.name = name;
requestPayload.provider = provider;
requestPayload.timestamp = new Date().getTime();
requestPayload.openPorts = openPorts;
requestPayload.imageId = imageId;
requestPayload.region = region;
requestPayload.location = [:];
requestPayload.location.country = locationCountry;
requestPayload.location.city = locationCity;
requestPayload.location.longitude = locationLongitude;
requestPayload.location.latitude = locationLatitude;
requestPayload.id = id;

//if (password != "") {
//    println "INFO: Using provided password"
requestPayload.ssh.password = password;
//}
requestPayload.ssh.key = privateKey;
requestPayload.ssh.fingerprint = fingerprint;

// Create a trust manager that does not validate certificate chains
TrustManager[] trustAllCerts = [ new X509TrustManager() {
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }
    public void checkClientTrusted(X509Certificate[] certs, String authType) {
    }
    public void checkServerTrusted(X509Certificate[] certs, String authType) {
    }
}
];

// Install the all-trusting trust manager
SSLContext sc = SSLContext.getInstance("SSL");
sc.init(null, trustAllCerts, new java.security.SecureRandom());
HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

// Create all-trusting host name verifier
HostnameVerifier allHostsValid = new HostnameVerifier() {
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
};

// Install the all-trusting host verifier
HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

def requestContent =  groovy.json.JsonOutput.toJson(requestPayload);
println requestContent
// Request execution
println "== Requesting baguette server for EMS deployment"
def emsConnection = new URL(emsUrl).openConnection();
emsConnection.setRequestMethod("POST")
emsConnection.setDoOutput(true)
emsConnection.setRequestProperty("Content-Type", "application/json")
emsConnection.setRequestProperty("Authorization", "Bearer " + baguetteBearer)
emsConnection.getOutputStream().write(requestContent.getBytes("UTF-8"));
def responseCode = emsConnection.getResponseCode();
def responseContent = emsConnection.getInputStream().getText();

// Feedback analysis
println "== Obtaining result:"
println ">> Result: Code=" + responseCode + " Content=" + responseContent
result = '{"Code"="' + responseCode + '","Content"="' + responseContent + '"}'