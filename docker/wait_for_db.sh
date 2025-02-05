#!/usr/bin/env bash

echo "Waiting for MariaDB to be ready..."

until nc -z "$DB_HOSTNAME" "$DB_PORT"; do
    echo "MariaDB is not ready yet..."
    sleep 5
done

echo "MariaDB is ready!"