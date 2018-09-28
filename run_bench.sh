#!/bin/bash

mvn clean package

mkdir out

for wrk_test in wrk_configs/[a-zA-A_]*.lua;
do
    echo "-------------------------------"
    test_name=`echo $wrk_test | sed -r "s/.*\/([a-zA-Z_]*)\.lua/\1/g"`
    echo "Starting test $test_name in $wrk_test"
    echo "----------------Starting Vert.x"
    java -jar vertx-json-validator-http-test/target/vertx-json-validator-http-test-1.0-SNAPSHOT-fat.jar -conf vertx-json-validator-http-test/conf.json &
    server_pid=$!
    sleep 2 # Wait server to start
    echo "Warmup"
    wrk -t2 -c100 -d5s -R1000 --latency -s "${wrk_test}" http://localhost:8083/$test_name &> /dev/null
    echo "Warmup completed"
    sleep 2
    echo "Starting test for Vert.x"
    wrk -t5 -c200 -d30s -R2000 --latency -s "${wrk_test}" "http://localhost:8083/$test_name" > out/${test_name}_vertx
    echo "----------Vert.x test completed"
    kill $server_pid

    echo "----------------Starting Everit"
    java -jar everit-json-validator-http-test/target/everit-json-validator-http-test-1.0-SNAPSHOT-fat.jar -conf everit-json-validator-http-test/conf.json &
    server_pid=$!
    sleep 2 # Wait server to start
    echo "Warmup"
    wrk -t2 -c100 -d5s -R1000 --latency -s "${wrk_test}" http://localhost:8081/$test_name &> /dev/null
    echo "Warmup completed"
    sleep 2
    echo "Starting test for Everit"
    wrk -t5 -c200 -d30s -R2000 --latency -s "${wrk_test}" "http://localhost:8081/$test_name" > out/${test_name}_everit
    echo "----------Everit test completed"
    kill $server_pid

    echo "----------------Starting NetworkNT"
    java -jar networknt-json-validator-http-test/target/networknt-json-validator-http-test-1.0-SNAPSHOT-fat.jar -conf networknt-json-validator-http-test/conf.json &
    server_pid=$!
    sleep 2 # Wait server to start
    echo "Warmup"
    wrk -t2 -c100 -d5s -R1000 --latency -s "${wrk_test}" http://localhost:8082/$test_name &> /dev/null
    echo "Warmup completed"
    sleep 2
    echo "Starting test for NetworkNT"
    wrk -t5 -c200 -d30s -R2000 --latency -s "${wrk_test}" "http://localhost:8082/$test_name" > out/${test_name}_networknt
    echo "----------NetworkNT test completed"
    kill $server_pid
done

echo "------------------------------"
echo "Benchmark completed"