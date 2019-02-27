@echo off
call gradle clean shadowJar
call mkdir cws-out
java -Dlog4j.configuration=file:log4j.properties -jar build/libs/cws-facilities-job-1.4-SNAPSHOT.jar ^
     -c config/cws-facility-job.yaml -l ./cws-out/ ^
     > ./cws-out/out_Facility.txt 2>&1
                                                                                                   