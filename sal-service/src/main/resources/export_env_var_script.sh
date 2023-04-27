# Environment variables preparation
if [ -z "$variables_requestedPortName" ]; then
    echo "[Env_var] No requested ports for this task. Nothing to be set."
else
    REQUESTED_PORT_NAME="PUBLIC_$variables_requestedPortName"
    PRIVATE_REQUESTED_PORT_NAME="PRIVATE_$variables_requestedPortName"

    if [[ ! -z $variables_requestedPortName ]]; then
        REQ="variables_${variables_requestedPortName}"
        REQUESTED_PORT_VALUE=${!REQ}

        REQ_PRV="variables_${variables_requestedPortName}_prv"
        PRV_REQUESTED_PORT_VALUE=${!REQ_PRV}

        if [[ -z ${!REQUESTED_PORT_NAME} ]]; then
            echo "[Env_var] Variable $REQUESTED_PORT_NAME does not exist. Exporting ..."
            export $REQUESTED_PORT_NAME=$REQUESTED_PORT_VALUE
            echo "[Env_var] $REQUESTED_PORT_NAME variable set to $REQUESTED_PORT_VALUE"
        fi

        if [[ -z ${!PRIVATE_REQUESTED_PORT_NAME} ]]; then
            echo "[Env_var] Variable $PRIVATE_REQUESTED_PORT_NAME does not exist. Exporting ..."
            export $PRIVATE_REQUESTED_PORT_NAME=$PRV_REQUESTED_PORT_VALUE
            echo "[Env_var] $PRIVATE_REQUESTED_PORT_NAME variable set to $PRV_REQUESTED_PORT_VALUE"
        fi
    fi
fi
