#!/usr/bin/env bash

# sudo gradle shadowJar
../gradlew shadowJar

rm -rf cws-out
mkdir cws-out

# . /Users/dsmith/cws_legacy/project/cwds-jobs/classpath.sh

java -Dlog4j.configuration=file:log4j.properties -jar build/libs/cws-facilities-job-1.4-SNAPSHOT.jar \
     -c /Users/dsmith/ramdisk/hide/facilities-cws-job.yml -l ./cws-out/ 

#\
# > ./cws-out/out_Facility.txt 2>&1
