PROVIDED_PORT_NAME=$variables_providedPortName

if [[ ! -z $PROVIDED_PORT_NAME ]]; then

    wget -q https://raw.githubusercontent.com/ow2-proactive/utility-scripts/main/network-scripts/Get_public_ip.sh && chmod +x Get_public_ip.sh || { echo "failed to download the IP script"; exit 1; }
    IP_ADDR=`./Get_public_ip.sh`
    CODE=$?
    echo "The Public IP was retreived with code: $CODE"

    if [[ $CODE -lt 6 ]]
    then
      echo Public adress: $IP_ADDR
    else
      echo "Getting the Public IP failed!"
    echo "The recieved IP: $IP_ADDR"
    exit 1
  fi

    echo "$IP_ADDR" > $PROVIDED_PORT_NAME"_ip"

    NODE_HOST_PRV_IP=$(echo ${variables_PA_NODE_HOST%%.*} | sed -e 's/ip-//' -e 's/-/./g')
    echo Private adress: $NODE_HOST_PRV_IP

    echo "$NODE_HOST_PRV_IP" > $PROVIDED_PORT_NAME"_prv_ip"
fi
