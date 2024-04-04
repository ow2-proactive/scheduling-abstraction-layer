
echo "Master start script"
WIREGUARD_VPN_IP=`ip a | grep wg | grep inet | awk '{print $2}' | cut -d'/' -f1`;
echo "WIREGUARD_VPN_IP= $WIREGUARD_VPN_IP";

sudo kubeadm init --apiserver-advertise-address ${WIREGUARD_VPN_IP} --service-cidr 10.96.0.0/16 --pod-network-cidr 10.244.0.0/16

echo "HOME: $(pwd), USERE: $(id -u -n)"
mkdir -p ~/.kube && sudo cp -i /etc/kubernetes/admin.conf ~/.kube/config && sudo chown $(id -u):$(id -g) ~/.kube/config
id -u ubuntu &> /dev/null

if [[ $? -eq 0 ]]
then
    #USER ubuntu is found
    mkdir -p /home/ubuntu/.kube && sudo cp -i /etc/kubernetes/admin.conf /home/ubuntu/.kube/config && sudo chown ubuntu:ubuntu /home/ubuntu/.kube/config
else
    echo "User Ubuntu is not found"
fi


#sudo -H -u ubuntu kubectl apply -f https://github.com/flannel-io/flannel/releases/latest/download/kube-flannel.yml;
sudo -H -u ubuntu bash -c 'helm repo add cilium https://helm.cilium.io/ && helm repo update'
sudo -H -u ubuntu bash -c 'helm install cilium cilium/cilium --namespace kube-system --set encryption.enabled=true --set encryption.type=wireguard'

echo "Setting KubeVela..."
#sudo -H -u ubuntu bash -c 'helm repo add kubevela https://kubevela.github.io/charts && helm repo update'
#sudo -H -u ubuntu bash -c 'nohup helm install --create-namespace -n vela-system kubevela kubevela/vela-core > /home/ubuntu/vela.txt 2>&1 &'
sudo -H -u ubuntu bash -c 'nohup vela install --version 1.8.2 > /home/ubuntu/vela.txt 2>&1 &'
