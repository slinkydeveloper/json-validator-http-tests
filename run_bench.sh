#!/bin/bash
IFS='\n'

mvn clean package

echo "Starting all servers"
java -jar everit-json-validator-http-test/target/everit-json-validator-http-test-1.0-SNAPSHOT-fat.jar -conf everit-json-validator-http-test/conf.json &
server_1_pid=$!
java -jar vertx-json-validator-http-test/target/vertx-json-validator-http-test-1.0-SNAPSHOT-fat.jar -conf vertx-json-validator-http-test/conf.json &
server_2_pid=$!
java -jar networknt-json-validator-http-test/target/networknt-json-validator-http-test-1.0-SNAPSHOT-fat.jar -conf networknt-json-validator-http-test/conf.json &
server_3_pid=$!

sleep 5 # Wait servers to start

mkdir out

for wrk_test in wrk_configs/[a-zA-A_]*.lua;
do
    echo "------------------------------"
    test_name=`echo $wrk_test | sed -r "s/.*\/([a-zA-Z_]*)\.lua/\1/g"`
    echo "Starting test $test_name in $wrk_test"
    echo "Warmup"
    wrk -t2 -c100 -d5s -R1000 --latency -s "${wrk_test}" http://localhost:8081/$test_name &> /dev/null &
    first_warmup=$!
    wrk -t2 -c100 -d5s -R1000 --latency -s "${wrk_test}" http://localhost:8082/$test_name &> /dev/null &
    second_warmup=$!
    wrk -t2 -c100 -d5s -R1000 --latency -s "${wrk_test}" http://localhost:8083/$test_name &> /dev/null &
    third_warmup=$!
    wait $first_warmup $second_warmup $third_warmup
    echo "Warmup completed"
    sleep 5
    echo "Starting test for Vert.x"
    wrk -t5 -c200 -d30s -R2000 --latency -s "${wrk_test}" "http://localhost:8083/$test_name" > out/${test_name}_vertx
    sleep 2
    echo "Starting test for Everit"
    wrk -t5 -c200 -d30s -R2000 --latency -s "${wrk_test}" "http://localhost:8081/$test_name" > out/${test_name}_everit
    sleep 2
    echo "Starting test for NetworkNt"
    wrk -t5 -c200 -d30s -R2000 --latency -s "${wrk_test}" "http://localhost:8082/$test_name" > out/${test_name}_networknt
    sleep 2
done

echo "------------------------------"
echo "Killing all servers"
kill $server_1_pid $server_2_pid $server_3_pid