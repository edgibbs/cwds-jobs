@echo off
rem call gradle clean shadowJar
rem call mkdir cap-out
java -Dlog4j.configuration=file:log4j.properties -jar build/libs/cap-users-job-1.4-SNAPSHOT.jar ^
     -c config/unix_cap-users-job.yaml -l ./cap-out/ ^
     > ./cap-out/out_cap_users.txt 2>&1