#!/bin/bash
echo "Worker pre-install script"

sudo -H -u ubuntu bash -c 'wget https://raw.githubusercontent.com/alijawadfahs/scripts/main/nebulous/nm-bootstrap-script.sh && chmod +x nm-bootstrap-script.sh'
sudo -H -u ubuntu bash -c './nm-bootstrap-script.sh "<CHANGE_ME>" "<CHANGE_ME>" "<CHANGE_ME>" "<CHANGE_ME>"'
sleep 60
