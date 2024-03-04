#!/bin/bash
echo "Master install script"

wget https://raw.githubusercontent.com/alijawadfahs/scripts/main/nebulous/install-kube-u22.sh && chmod +x ./install-kube-u22.sh && ./install-kube-u22.sh

sudo -H -u ubuntu bash -c ' curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 && chmod 700 get_helm.sh && ./get_helm.sh'
# Add KubeVela Helm repository and update
echo "Setting up KubeVela..."
sudo -H -u ubuntu bash -c 'helm repo add kubevela https://kubevelacharts.oss-cn-hangzhou.aliyuncs.com/core && helm repo update'

echo "Configuration complete."
