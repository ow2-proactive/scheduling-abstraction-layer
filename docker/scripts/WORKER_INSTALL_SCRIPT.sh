#!/bin/bash
echo "Worker install script"
wget https://github.com/ow2-proactive/scheduling-abstraction-layer/tree/master/docker/bootstrap-scripts/install-kube-u22.sh && chmod +x ./install-kube-u22.sh && ./install-kube-u22.sh
