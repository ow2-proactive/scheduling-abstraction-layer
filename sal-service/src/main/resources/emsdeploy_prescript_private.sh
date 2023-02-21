echo "== Configuring infrastructure resource"
echo "-- Generating EMS-Specific keypair"
ssh-keygen -t rsa -m pkcs8 -f /tmp/ems-keypair
echo "-- Getting the private IP address of the infrastructure"
interface=$(route -n | sed -n '3 p' | sed 's/  */ /g' | cut -d" " -f8)
ifconfig $interface | grep 'inet ' | sed 's/  */ /g' | cut -d" " -f 3 > /tmp/tmp.txt
tr -d "\n" < /tmp/tmp.txt > /tmp/ip.txt
echo "Found IP for interface \"$interface\":"
cat /tmp/ip.txt
echo "-- Adding generated public key"
cat /tmp/ems-keypair.pub >> ~/.ssh/authorized_keys
