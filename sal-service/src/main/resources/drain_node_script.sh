sudo -H -u ubuntu bash -c "kubectl drain $variables_nodeName --ignore-daemonsets"
sudo -H -u ubuntu bash -c "kubectl delete node $variables_nodeName"
