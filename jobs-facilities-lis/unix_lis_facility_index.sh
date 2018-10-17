#!/usr/bin/env bash

sudo gradle shadowJar

rm -rf lis-out
mkdir lis-out

java -Dlog4j.configuration=file:log4j.properties -jar build/libs/lis-facilities-job-1.3-SNAPSHOT.jar \
     -c config/unix_lis-facility-job.yaml -l ./lis-out/ \
     > ./lis-out/out_Facility.txt 2>&1