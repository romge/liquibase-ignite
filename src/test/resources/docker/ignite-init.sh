#!/usr/bin/env bash

/usr/local/bin/docker-entrypoint.sh &
server_pid=$!

echo "Waiting for server to start...."
cluster_ready=false
for i in {1..50}; do
  if /opt/ignite3cli/bin/ignite3 cluster init --name lbCluster --config-files /docker-entrypoint-initdb.d/ignite-config.conf; then
    cluster_ready=true
    echo "Ignite cluster initialized, trying to connect..."
    break
  fi
  echo "Cluster is not yet ready, trying again..."
  sleep 5
done
if [ "$cluster_ready" = false ]; then
  echo "Timeout while waiting for server to start."
  kill $server_pid
  exit 1
fi
if [ "$cluster_ready" = true ]; then
echo "Connecting to the database..."
connected=false
for j in {1..50}; do
  if /opt/ignite3cli/bin/ignite3 sql --file /docker-entrypoint-initdb.d/ignite-init.sql --jdbc-url "jdbc:ignite:thin://localhost?username=lbuser&password=LiquibasePass1"; then
    connected=true
    echo "Successfully connected."
    break
  fi
  echo "Could not connect, trying again..."
  sleep 5
done
if [ "$connected" = false ]; then
  echo "Connection failed."
  kill $server_pid
  exit 1
fi
fi

wait $server_pid
