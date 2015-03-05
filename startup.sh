#!/bin/bash

rm -r logs
mkdir logs
for i in {0..10}
do
  port=$((i+3000))
  nohup java -jar P2PNode/target/P2PNode-1.0-SNAPSHOT-jar-with-dependencies.jar -u dim -h 127.0.0.1 -p ${port} > logs/node${i}.out &
  sleep 1
  echo "Node ${i} was started"
done
