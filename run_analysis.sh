#!/bin/bash

params_file="analysis.txt"

docker-compose up -d db

counter=1
while IFS= read -r param
do
  container_name="java-app-$counter"
  export PARAMS="$param"

  docker-compose run -d --name "$container_name" java-app

  echo "Started container: $container_name with cmd: $param"

  counter=$((counter + 1))
done < "$params_file"