echo BEGIN "$variables_PA_TASK_NAME"

################################################################################
### THIS PART IS IMAGE SPECIFIC. IF YOU NEED TO MODIFY SOMETHING, DO IT HERE ###

DOCKER_IMAGE=$variables_DOCKER_IMAGE
PORTS=$variables_PORTS
ENV_VARS=$variables_ENV_VARS

################################################################################

# Manually find a free random port to preserve it in case if docker (re)start (Pause/Resume)
GET_RANDOM_PORT(){
    PCA_SERVICES_PORT_RANGE_FILE=$variables_PA_SCHEDULER_HOME/config/pca_services_port_range
    if [[ -f "$PCA_SERVICES_PORT_RANGE_FILE" ]]; then
        read LOWERPORT UPPERPORT < $PCA_SERVICES_PORT_RANGE_FILE
    else
        read LOWERPORT UPPERPORT < /proc/sys/net/ipv4/ip_local_port_range
    fi
    while :
    do
        RND_PORT="`shuf -i $LOWERPORT-$UPPERPORT -n 1`"
        ss -lpn | grep -q ":$RND_PORT " || break
    done
    echo $RND_PORT
}

if [ "$PORTS" = "" ]; then
    echo "[INFO] Picking a random port number"
    PORTS=$(GET_RANDOM_PORT)":"$(GET_RANDOM_PORT)
    echo $PORTS
fi
echo "The service will be initialized on ports $PORTS"

INSTANCE_NAME=$variables_INSTANCE_NAME

if [ -z "$INSTANCE_NAME" ]; then
    echo ERROR: The INSTANCE_NAME is not provided by the user. Empty value is not allowed.
    exit 1
fi

echo "Pulling "$variables_PA_JOB_NAME" image"
docker pull $DOCKER_IMAGE

if [ "$(docker ps -a --format '{{.Names}}' | grep "^$INSTANCE_NAME$")" ]; then
    echo [ERROR] "$INSTANCE_NAME" is already used by another service instance.
    exit 128
else
    eval echo "docker run --name $INSTANCE_NAME -p $PORTS $ENV_VARS -d $DOCKER_IMAGE"
    eval "docker run --name $INSTANCE_NAME -p $PORTS $ENV_VARS -d $DOCKER_IMAGE"
fi

containerID=$(docker ps -aqf "name=^$INSTANCE_NAME$")
echo "$containerID" > $INSTANCE_NAME"_containerID"

echo END "$variables_PA_TASK_NAME"
