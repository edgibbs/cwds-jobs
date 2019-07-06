#!/usr/bin/env bash

sudo gradle shadowJar

rm -rf cws-out
mkdir cws-out

java -Dlog4j.configuration=file:log4j.properties -jar build/libs/cap-users-job-1.4-SNAPSHOT.jar -r \
     -c config/unix_cap-users-job.yaml -l ./cws-out/ \
     > ./cws-out/out_CAP_USERS.txt 2>&1
