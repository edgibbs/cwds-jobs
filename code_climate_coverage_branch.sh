#!/bin/bash

CC_TEST_REPORTER_ID='de72d96dca2983c3f95d8ddecc1b489a6f51cafd5548d896e213659aedc75d0e'
last_commit=$(git rev-parse HEAD)
echo "${last_commit}"
commited_at=$(git log -1 --pretty=format:'%ct')
echo "${commited_at}"
export GIT_COMMIT=${last_commit}
export GIT_COMMIT_SHA=${last_commit}
export GIT_COMMITED_AT=${commited_at}
export GIT_BRANCH=${pull_request_event_head_repo_default_branch}
JACOCO_SOURCE_PATH=jobs-audit-events/src/main/java ./cc-test-reporter format-coverage -d -t jacoco jobs-audit-events/build/reports/jacoco/test/jacocoTestReport.xml -o coverage/codeclimate.audit-events.jacoco.json
JACOCO_SOURCE_PATH=jobs-cap-users/src/main/java ./cc-test-reporter format-coverage -d -t jacoco jobs-cap-users/build/reports/jacoco/test/jacocoTestReport.xml -o coverage/codeclimate.cap-users.jacoco.json
JACOCO_SOURCE_PATH=jobs-common/src/main/java ./cc-test-reporter format-coverage -d -t jacoco jobs-common/build/reports/jacoco/test/jacocoTestReport.xml -o coverage/codeclimate.common.jacoco.json
JACOCO_SOURCE_PATH=jobs-facilities-common/src/main/java ./cc-test-reporter format-coverage -d -t jacoco jobs-facilities-common/build/reports/jacoco/test/jacocoTestReport.xml -o coverage/codeclimate.fac-common.jacoco.json
JACOCO_SOURCE_PATH=jobs-facilities-cws/src/main/java ./cc-test-reporter format-coverage -d -t jacoco jobs-facilities-cws/build/reports/jacoco/test/jacocoTestReport.xml -o coverage/codeclimate.fac-cws.jacoco.json
JACOCO_SOURCE_PATH=jobs-facilities-lis/src/main/java ./cc-test-reporter format-coverage -d -t jacoco jobs-facilities-lis/build/reports/jacoco/test/jacocoTestReport.xml -o coverage/codeclimate.fac-lis.jacoco.json
./cc-test-reporter sum-coverage coverage/codeclimate.*.json -p 6
./cc-test-reporter upload-coverage --debug -r ${CC_TEST_REPORTER_ID}