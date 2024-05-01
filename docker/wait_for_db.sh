#!/usr/bin/env bash

# Loop until the MariaDB port is listening
while ! nc -z $DB_HOSTNAME $DB_PORT; do
    echo "Port $DB_PORT is not yet listening, waiting..."
    sleep 5
done

echo "Port $DB_PORT is now listening!"
