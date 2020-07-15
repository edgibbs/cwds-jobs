import groovy.transform.Field

@Library('jenkins-pipeline-utils') _

@Field
def GITHUB_CREDENTIALS_ID = '433ac100-b3c2-4519-b4d6-207c029a103b'

@Field
def serverArti
@Field
def rtGradle

@Field
def tagPrefixes = ['audit-events', 'cap-users', 'facilities-cws', 'facilities-lis']
@Field
def newTag
@Field
def tagPrefix
@Field
def newVersion
@Field
def overrideVersion
@Field
def releaseProject

def githubConfig() {
  githubConfigProperties('https://github.com/ca-cwds/cwds-jobs')
}

node ('dora-slave') {
  if (env.BUILD_JOB_TYPE == 'master') {
    // for master pipeline set the branch specifier on config UI to: master
    def triggerProperties = pullRequestMergedTriggerProperties('cwds-jobs-master')
    properties([
      [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: true],
      githubConfig(),
      disableConcurrentBuilds(),
      pipelineTriggers([triggerProperties]),
      buildDiscarderDefaults('master')
    ])
  } else if (env.BUILD_JOB_TYPE == 'hotfix') {
    // for hotfix pipeline set the branch specifier on config UI to: ${branch} with the "Lightweight checkout" checkbox disabled
    properties([
      [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false],
      githubConfig(),
      disableConcurrentBuilds(),
      buildDiscarderDefaults('master'),
      parameters([
        string(defaultValue: 'master', description: '', name: 'branch'),
        choice(choices: tagPrefixes, description: 'tag prefix', name: 'TAG_PREFIX'),
        string(defaultValue: '', description: 'Fill this field if need to specify custom version ', name: 'OVERRIDE_VERSION')
      ])
    ])
  } else { // BUILD_JOB_TYPE=pull_request
    // for PR pipeline set the branch specifier on config UI to: ${ghprbActualCommit} with the "Lightweight checkout" checkbox disabled
    def triggerProperties = githubPullRequestBuilderTriggerProperties()
    properties([
      [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: true],
      githubConfig(),
      disableConcurrentBuilds(),
      pipelineTriggers([triggerProperties]),
      buildDiscarderDefaults()
    ])
  }

  try {
    stage('Preparation') {
      serverArti = Artifactory.server 'CWDS_DEV'
      rtGradle = Artifactory.newGradleBuild()
      rtGradle.tool = 'Gradle_35'
      rtGradle.resolver repo: 'repo', server: serverArti
      rtGradle.deployer.mavenCompatible = true
      rtGradle.deployer.deployMavenDescriptors = true
      rtGradle.useWrapper = true
      overrideVersion = ''
      releaseProject = true
      if (env.BUILD_JOB_TYPE == 'hotfix') {
        if (OVERRIDE_VERSION == '') {
          throw new Exception('OVERRIDE_VERSION parameter is mandatory for hotfix builds')
        }
        if (branch == '') {
          throw new Exception('branch parameter is mandatory for hotfix builds')
        }
        overrideVersion = OVERRIDE_VERSION
        tagPrefix = TAG_PREFIX
        newTag = "${tagPrefix}-${overrideVersion}"
        newVersion = ''
      }
      cleanWs()
      checkout scm
    }
    if (env.BUILD_JOB_TYPE == 'pull_request') {
      stage('Check for Labels') {
        checkForLabel('cwds-jobs', tagPrefixes)
      }
    }
    if (env.BUILD_JOB_TYPE == 'master') {
      stage('Increment Tag') {
        newTag = newSemVer('', tagPrefixes)
        (tagPrefix, newVersion) = (newTag =~ /^(.+)\-(\d+\.\d+\.\d+)/).with { it[0][1,2] }
      }
    }
    if (env.BUILD_JOB_TYPE == 'master' || env.BUILD_JOB_TYPE == 'hotfix') {
      stage('Build'){
        rtGradle.run buildFile: "jobs-${tagPrefix}/build.gradle".toString(), tasks: "jar shadowJar -DRelease=${releaseProject} -D build=${env.BUILD_NUMBER} -DCustomVersion=${overrideVersion} -DnewVersion=${newVersion}".toString()
      }
    }
	stage('Init CodeClimate') {
		sh "curl -L https://codeclimate.com/downloads/test-reporter/test-reporter-latest-linux-amd64 > ./cc-test-reporter"
		sh "chmod +x ./cc-test-reporter"
		sh "./cc-test-reporter before-build --debug"
	}
    stage('Tests and Coverage') {
      rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'test jacocoMergeTest'
    }
    stage('SonarQube analysis'){
      lint(rtGradle)
    }
	stage('Publish Coverage Reports') {
		sh "chmod 755 code_climate_coverage_branch.sh"
		output = sh(returnStdout: true, script: './code_climate_coverage_branch.sh').trim()
	}
    if (env.BUILD_JOB_TYPE == 'master') {
      stage('Update License Report') {
        updateLicenseReport('master', GITHUB_CREDENTIALS_ID, [runtimeGradle: rtGradle])
      }
    }
    if (env.BUILD_JOB_TYPE == 'master' || env.BUILD_JOB_TYPE == 'hotfix') {
      stage('Tag Repo') {
        tagGithubRepo(newTag, GITHUB_CREDENTIALS_ID)
      }
      stage('Push to artifactory') {
        rtGradle.deployer.deployArtifacts = true
        rtGradle.run buildFile: "jobs-${tagPrefix}/build.gradle".toString(), tasks: "publish -DRelease=${releaseProject} -DBuildNumber=${env.BUILD_NUMBER} -DCustomVersion=${overrideVersion} -DnewVersion=${newVersion}".toString()
        rtGradle.deployer.deployArtifacts = false
      }
      stage('Clean WorkSpace') {
        archiveArtifacts artifacts: '**/jobs-*.jar,readme.txt,DocumentIndexerJob-*.jar', fingerprint: true
        sh ('docker-compose down -v')
        publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: '**/build/reports/tests/', reportFiles: 'index.html', reportName: 'JUnitReports', reportTitles: ''])
      }
    }
  } catch(Exception e) {
    echo e.message
    publishHTML([allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: '**/build/reports/tests/', reportFiles: 'index.html', reportName: 'JUnitReports', reportTitles: ''])
    sh ('docker-compose down -v')
    emailext attachLog: true, body: "Failed: ${e}", recipientProviders: [[$class: 'DevelopersRecipientProvider']],
    subject: "Jobs failed with ${e.message}", to: "Alex.Serbin@osi.ca.gov"
    slackSend channel: '#cals-api', baseUrl: 'https://hooks.slack.com/services/', tokenCredentialId: 'slackmessagetpt2', message: "Build Falled: ${env.JOB_NAME} ${env.BUILD_NUMBER}"
    currentBuild.result = 'FAILURE'
    throw e
  } finally {
    cleanWs()
  }
}
