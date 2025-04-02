#!/bin/bash

params_file="analysis.txt"

docker-compose up -d --force-recreate db

counter=1
while IFS= read -r param
do
  container_name="java-app-$counter"
  export PARAMS="$param"

  docker rm -f "$container_name" 2>/dev/null || true
  docker-compose run -d --name "$container_name" java-app

  echo "Started container: $container_name with cmd: $param"

  counter=$((counter + 1))
done < "$params_file"