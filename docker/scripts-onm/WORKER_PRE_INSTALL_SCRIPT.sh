
echo "Worker pre-install script"
sudo hostnamectl set-hostname "$variables_PA_JOB_NAME"
sudo -H -u ubuntu bash -c 'wget https://github.com/ow2-proactive/scheduling-abstraction-layer/tree/master/docker/bootstrap-scripts/nm-bootstrap-script.sh && chmod +x nm-bootstrap-script.sh'
sudo -H -u ubuntu bash -c "./nm-bootstrap-script.sh 'CREATE' 'WORKER' $APP_UUID $ONM_IP";

WIREGUARD_VPN_IP=`ip a | grep wg | grep inet | awk '{print $2}' | cut -d'/' -f1`;
echo "WIREGUARD_VPN_IP= $WIREGUARD_VPN_IP";
