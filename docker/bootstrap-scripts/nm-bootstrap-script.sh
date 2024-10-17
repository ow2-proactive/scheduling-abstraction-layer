#!/bin/bash

# "CREATE" or "DELETE" Overlay Node
ACTION=$1
# Define Application Node Type ("MASTER","WORKER")
NODE_TYPE=$2
# Application UUID
APPLICATION_UUID=$3
# Overlay Network Manager Public IP
ONM_IP=$4

# Get the public IP
public_ip=$(curl -s http://httpbin.org/ip | grep -oP '(?<="origin": ")[^"]*')

# Get the Application UUID from the environment variable
application_uuid=$APPLICATION_UUID

# Get the currently logged in user (assuming single user login)
logged_in_user=$(whoami)

# Get the isMaster variable from the environment variable
if [ "$NODE_TYPE" == "MASTER" ]; then
  IS_MASTER="true";
elif [ "$NODE_TYPE" == "WORKER" ]; then
  IS_MASTER="false"
fi

# Check if string1 is equal to string2
if [ "$ACTION" == "CREATE" ]; then
  echo "Creating OpenSSH Public/Private Key Pair..."
  # Create Wireguard Folder to accept the wireguard scripts
  mkdir -p /home/${logged_in_user}/wireguard

  # Create OpenSSH Public/Private Key files
  ssh-keygen -C wireguard-pub -t rsa -b 4096 -f /home/${logged_in_user}/wireguard/wireguard -N ""

  cat /home/${logged_in_user}/wireguard/wireguard.pub >> /home/${logged_in_user}/.ssh/authorized_keys
fi

PRIVATE_KEY_FILE=$(cat /home/${logged_in_user}/wireguard/wireguard | base64 | tr '\n' ' ')

PAYLOAD=$(cat <<EOF
{
  "privateKeyBase64": "${PRIVATE_KEY_FILE}",
  "publicKey": "$(</home/${logged_in_user}/wireguard/wireguard.pub)",
  "publicIp": "${public_ip}",
  "applicationUUID": "${application_uuid}",
  "sshUsername": "${logged_in_user}",
  "isMaster": "$IS_MASTER"
}
EOF
)

echo "$PAYLOAD"

if [ "$ACTION" == "CREATE" ]; then
  curl -v -X POST -H "Content-Type: application/json" -d "$PAYLOAD" http://${ONM_IP}:8082/api/v1/node/create
elif [ "$ACTION" == "DELETE" ]; then
  curl -v -X DELETE -H "Content-Type: application/json" -d "$PAYLOAD" http://${ONM_IP}:8082/api/v1/node/delete
fi
