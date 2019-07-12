import groovy.transform.Field
@Library('jenkins-pipeline-utils') _

@Field
def githubCredentialsId = '433ac100-b3c2-4519-b4d6-20node7c029a103b'
@Field
def deAnsibleGithubUrl = 'git@github.com:ca-cwds/de-ansible.git'

/*
Input from configuration on Jenkins UI:
* job_name - value should be one one of: audit-events, cap-users, facilities-cws, facilities-lis
* version - version of application to deploy
* Job_StartScript - values: unsealed, sealed; used only for facilities-cws, facilities-lis
* Java_heap_size
* Reset_JobLastRun_time
* remove_index
 */

deploy('integration')

def deploy(environment) {
  node(environment) {
    def props = prepareProperties(environment, env.version, env.job_name)
    try {
      checkoutStage(environment)
      deployStage(environment, env.version, props)
      updateManifestStage(environment, env.version, props)
    } catch(Exception e) {
      currentBuild.result = 'FAILURE'
      throw e
    } finally {
      cleanWs()
    }
  }
}

def prepareProperties(environment, version, cwdsJobName) {
  def cwdsJobProps = [
    'audit-events': [
      'MANIFEST_KEY': 'auditevents',
      'PLAYBOOK': 'run-jobs-audit-events.yml',
      'PLAYBOOK_ENV': "-e remove_index=${env.remove_index} -e VERSION_NUMBER=$version",
      'DASHBOARD': [
        'componentName': 'auditevents',
        'packageName': 'auditevents'
      ]
    ],
    'cap-users': [
      'MANIFEST_KEY': 'cap-users-index-job',
      'PLAYBOOK': 'deploy-cap-jobs-to-rundeck.yml',
      'PLAYBOOK_ENV': "-e remove_cap_user_index=${env.remove_index} -e CAP_VERSION_NUMBER=$version",
      'DASHBOARD': [
        'componentName': 'RUNDECK',
        'packageName': 'RUNDECK'
      ]
    ],
    'facilities-cws': [
      'MANIFEST_KEY': 'cals-jobs',
      'PLAYBOOK': 'deploy-facility-job-to-rundeck.yml',
      'PLAYBOOK_ENV': "-e remove_facility_index=${env.remove_index} -e Job_StartScript=${env.Job_StartScript} -e CALS_VERSION_NUMBER=$version",
      'DASHBOARD': [
        'componentName': 'RUNDECK',
        'packageName': 'RUNDECK-FACILITY'
      ]
    ],
    'facilities-lis': [
      'MANIFEST_KEY': 'cals-jobs',
      'PLAYBOOK': 'deploy-facility-job-to-rundeck.yml',
      'PLAYBOOK_ENV': "-e remove_facility_index=${env.remove_index} -e Job_StartScript=${env.Job_StartScript} -e CALS_VERSION_NUMBER=$version",
      'DASHBOARD': [
        'componentName': 'RUNDECK',
        'packageName': 'RUNDECK-FACILITY'
      ]
    ]
  ]
  def props = cwdsJobProps[cwdsJobName]
  props.PLAYBOOK_ENV += " -e Java_heap_size=${env.Java_heap_size} -e JobLastRun_time=${env.Reset_JobLastRun_time}"
  props.DASHBOARD['nameOfEnv'] = 'Integration'
  props
}

def checkoutStage(environment) {
  stage("Checkout for $environment") {
    deleteDir()
    checkout scm
  }
}

def deployStage(environment, version, props) {
  stage("Deploy to $environment") {
    ws {
      environmentDashboard(addColumns: false, buildJob: '', buildNumber: version,
        componentName: props.DASHBOARD.componentName, data: [],
        nameOfEnv: props.DASHBOARD.nameOfEnv,
        packageName: props.DASHBOARD.packageName
      ) {
        git branch: 'master', credentialsId: githubCredentialsId, url: deAnsibleGithubUrl
        sh "ansible-playbook ${props.PLAYBOOK_ENV} -i inventories/$environment/hosts.yml ${props.PLAYBOOK} --vault-password-file ~/.ssh/vault.txt"
      }
    }
  }
}

def updateManifestStage(environment, version, props) {
  stage("Update Manifest for $environment") {
    updateManifest(props.MANIFEST_KEY, environment, githubCredentialsId, version)
  }
}
