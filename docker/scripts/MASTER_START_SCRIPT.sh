#!/bin/bash
echo "Master start script"

sudo kubeadm init --pod-network-cidr 10.244.0.0/16

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


sudo -H -u ubuntu kubectl apply -f https://github.com/flannel-io/flannel/releases/latest/download/kube-flannel.yml;

echo "Setting KubeVela..."
#sudo -H -u ubuntu bash -c 'helm repo add kubevela https://kubevela.github.io/charts && helm repo update'
#sudo -H -u ubuntu bash -c 'nohup helm install --create-namespace -n vela-system kubevela kubevela/vela-core > /home/ubuntu/vela.txt 2>&1 &'
sudo -H -u ubuntu bash -c 'nohup vela install --version 1.8.2 > /home/ubuntu/vela.txt 2>&1 &'
