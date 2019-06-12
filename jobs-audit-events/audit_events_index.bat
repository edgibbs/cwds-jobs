@echo off
call gradle clean shadowJar
call mkdir job-out
java -Dlog4j.configuration=file:log4j.properties -jar build/libs/audit-events-job-1.4-SNAPSHOT.jar ^
     -c config/unix_audit-events-job.yaml -l ./job-out/ ^
     > ./job-out/out_audit_events.txt 2>&1