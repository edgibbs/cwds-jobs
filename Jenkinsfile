@Library('jenkins-pipeline-utils') _

node ('dora-slave'){
   def artifactVersion="3.3-SNAPSHOT"
   def serverArti = Artifactory.server 'CWDS_DEV'
   def rtGradle = Artifactory.newGradleBuild()
   if (env.BUILD_JOB_TYPE=="master" ) {
     triggerProperties = pullRequestMergedTriggerProperties('cwds-jobs-master')
     properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '25')),
     pipelineTriggers([triggerProperties]), disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
     parameters([
        booleanParam(defaultValue: true, description: '', name: 'USE_NEWRELIC'),
        string(defaultValue: 'latest', description: '', name: 'APP_VERSION'),
        string(defaultValue: 'master', description: '', name: 'branch'),
        booleanParam(defaultValue: true, description: 'Default release version template is: <majorVersion>_<buildNumber>-RC', name: 'RELEASE_PROJECT'),
        string(defaultValue: "", description: 'Fill this field if need to specify custom version ', name: 'OVERRIDE_VERSION'),
        string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory')])
        ])
   } else {
      properties([disableConcurrentBuilds(), [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
      parameters([
        string(defaultValue: 'master', description: '', name: 'branch'),
        booleanParam(defaultValue: true, description: 'Default release version template is: <majorVersion>_<buildNumber>-RC', name: 'RELEASE_PROJECT'),
        string(defaultValue: 'inventories/tpt2dev/hosts.yml', description: '', name: 'inventory')])])
   }

  try {
   stage('Preparation') {
       cleanWs()
       git branch: '$branch', credentialsId: '433ac100-b3c2-4519-b4d6-207c029a103b', url: 'git@github.com:ca-cwds/cals-jobs.git'
       rtGradle.tool = "Gradle_35"
       rtGradle.resolver repo:'repo', server: serverArti
       rtGradle.deployer.mavenCompatible = true
       rtGradle.deployer.deployMavenDescriptors = true
       rtGradle.useWrapper = true
   }
   if (env.BUILD_JOB_TYPE=="master" ) {
        stage('Increment Tag') {
           newTag = newSemVer()
           echo newTag
        }
   } else {
     stage('Check for Label') {
        checkForLabel("cwds-jobs")
     }
   }
   stage('Build'){
       def buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: "jar shadowJar -D build=${BUILD_NUMBER} -DnewVersion=${env.newTag}".toString()
   }
   stage('Tests and Coverage') {
       buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'test jacocoMergeTest'
   }
   stage('SonarQube analysis'){
       withSonarQubeEnv('Core-SonarQube') {
         buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'sonarqube'
       }
   }
   if (env.BUILD_JOB_TYPE=="master" ) {
        stage ('Push to artifactory'){
            rtGradle.deployer.deployArtifacts = true
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: "publish -D build=${BUILD_NUMBER} -DnewVersion=${newTag}".toString()
            tGradle.deployer.deployArtifacts = false
        }
        stage('Clean WorkSpace') {
            archiveArtifacts artifacts: '**/jobs-*.jar,readme.txt,DocumentIndexerJob-*.jar', fingerprint: true
            sh ('docker-compose down -v')
            publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: '**/build/reports/tests/', reportFiles: 'index.html', reportName: 'JUnitReports', reportTitles: ''])
        }
    }
 } catch(Exception e) {
       publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: '**/build/reports/tests/', reportFiles: 'index.html', reportName: 'JUnitReports', reportTitles: ''])
       sh ('docker-compose down -v')
       emailext attachLog: true, body: "Failed: ${e}", recipientProviders: [[$class: 'DevelopersRecipientProvider']],
       subject: "Jobs failed with ${e.message}", to: "Leonid.Marushevskiy@osi.ca.gov, Alex.Kuznetsov@osi.ca.gov"
       slackSend channel: "#cals-api", baseUrl: 'https://hooks.slack.com/services/', tokenCredentialId: 'slackmessagetpt2', message: "Build Falled: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
       currentBuild.result = "FAILURE"
       throw e
    }finally {
        cleanWs()
    }
}
