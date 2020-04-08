#!/usr/bin/env bash

sudo gradle shadowJar

rm -rf cws-out
mkdir cws-out

java -Dlog4j.configuration=file:log4j.properties -jar build/libs/cws-facilities-job-1.4-SNAPSHOT.jar \
     -c config/unix_cws_facility-job.yaml -l ./cws-out/ \
     > ./cws-out/out_Facility.txt 2>&1

