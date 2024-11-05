#!/bin/bash

# This bash script is designed to prepare and install docker and Kubernetes v1.26 for Ubuntu 22.04.
# If an error occurs, the script will exit with the value of the PID to point at the logfile.
# Author: Ali Jawad FAHS, Ankica Barisic, Activeeon

# Set up the script variables
STARTTIME=$(date +%s)
PID=$(echo $$)
EXITCODE=$PID
DATE=$(date)
LOGFILE="/var/log/kube-install.$PID.log"

# Set up the logging for the script
sudo touch $LOGFILE
sudo chown $USER:$USER $LOGFILE

# All the output of this shell script is redirected to the LOGFILE
exec 3>&1 4>&2
trap 'exec 2>&4 1>&3' 0 1 2 3
exec 1>$LOGFILE 2>&1

# A function to print a message to the stdout as well as the LOGFILE
log_print(){
  level=$1
  Message=$2
  echo "$level [$(date)]: $Message"
  echo "$level [$(date)]: $Message" >&3
  }

# A function to check for the apt lock
Check_lock() {
    i=0
    log_print INFO "Checking for apt lock"
    while [ "$(ps aux | grep [l]ock_is_held | wc -l)" != "0" ]; do
        echo "Lock_is_held $i"
        ps aux | grep [l]ock_is_held
        sleep 10
        ((i=i+10))
    done
    log_print INFO "Exited the while loop, time spent: $i"
    echo "ps aux | grep apt"
    ps aux | grep apt
    log_print INFO "Waiting for lock task ended properly."
}

# Start the Configuration
log_print INFO "Configuration started!"
log_print INFO "Logs are saved at: $LOGFILE"

# Update the package list
log_print INFO "Updating the package list."
sudo apt-get update
sudo unattended-upgrade -d

# Check for lock
Check_lock

# Install curl
log_print INFO "Installing curl"
sudo apt-get install -y curl || { log_print ERROR "curl installation failed!"; exit $EXITCODE; }

# Install Docker
log_print INFO "Installing Docker"
sudo apt-get install -y docker.io
sudo systemctl enable docker
sudo systemctl status docker
sudo systemctl start docker

sudo docker -v || { log_print ERROR "Docker installation failed!"; exit $EXITCODE; }

# Add the Kubernetes GPG key
# log_print INFO "Adding Kubernetes GPG key"
#curl -fsSL https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo gpg --dearmor -o  /etc/apt/keyrings/kubernetes-apt-keyring.gpg || { log_print ERROR "Failed to add new Kubernetes GPG key"; exit $EXITCODE; }

# Adding Kubernetes Repo
log_print INFO "Adding Kubernetes Repo"
sudo mkdir -p /etc/apt/keyrings
echo "deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.26/deb/ /" | sudo tee /etc/apt/sources.list.d/kubernetes.list

# Check for lock
Check_lock
# curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.26/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg || { log_print ERROR "Kubernetes repo can't be added!"; exit $EXITCODE; }
curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.31/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg || { log_print ERROR "Kubernetes repo can't be added!"; exit $EXITCODE; }
sudo apt-get update

# Check for lock
Check_lock

# Install Kubernetes
log_print INFO "Installing Kubernetes"
sudo apt-get install -y kubeadm=1.26.15-1.1 --allow-downgrades || { log_print ERROR "kubeadm installation failed!"; exit $EXITCODE; }
sudo apt-get install -y kubelet=1.26.15-1.1 --allow-downgrades || { log_print ERROR "kubectl installation failed!"; exit $EXITCODE; }
sudo apt-get install -y kubectl=1.26.15-1.1 --allow-downgrades || { log_print ERROR "kubelet installation failed!"; exit $EXITCODE; }


# Hoding upgrades for Kubernetes software (versions to updated manually)
sudo apt-mark hold kubeadm kubelet kubectl

# Checking for the installiation versions

sudo mkdir /etc/containerd
containerd config default | sudo tee /etc/containerd/config.toml

sudo sed -i 's/SystemdCgroup = false/SystemdCgroup = true/' /etc/containerd/config.toml
sudo systemctl restart containerd


log_print INFO "Checking Kubernetes versions"

kubeadm version     || { log_print ERROR "kubeadm installation failed!"; exit $EXITCODE; }
kubectl version
if [ $? -gt 1 ]
then
    log_print ERROR "kubectl installation failed!"; exit $EXITCODE;
fi
kubelet --version   || { log_print ERROR "kubelet installation failed!"; exit $EXITCODE; }


# Turn off the swap momery
if [ `grep Swap /proc/meminfo | grep SwapTotal: | cut -d" " -f14` == "0" ];
    then
        log_print INFO "The swap memory is Off"
    else
        sudo swapoff –a || { log_print ERROR "swap memory can't be turned off "; exit $EXITCODE; }
    fi


# Declare configuration done successfully
ENDTIME=$(date +%s)
ELAPSED=$(( ENDTIME - STARTTIME ))
log_print INFO "Configuration done successfully in $ELAPSED seconds "
