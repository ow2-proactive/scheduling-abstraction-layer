echo "== Configuring infrastructure resource"
echo "-- Generating EMS-Specific keypair"
ssh-keygen -t rsa -m pkcs8 -f /tmp/ems-keypair
echo "-- Getting the public IP address of the infrastructure"
curl -o /tmp/ip.txt ifconfig.me
echo Found IP:
cat /tmp/ip.txt
echo "-- Adding generated public key"
cat /tmp/ems-keypair.pub >> ~/.ssh/authorized_keys