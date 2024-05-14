#!/bin/bash
echo "Worker start script"
sudo kubeadm reset --force
echo $variables_kubeCommand
sudo $variables_kubeCommand
