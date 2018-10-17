@echo off
call gradle clean shadowJar
call mkdir lis-out
java -Dlog4j.configuration=file:log4j.properties -jar build/libs/lis-facilities-job-1.2-SNAPSHOT.jar ^
     -c config/lis-facility-job.yaml -l ./lis-out/ ^
     > ./lis-out/out_Facility.txt 2>&1
