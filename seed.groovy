import utilities.SCM
import utilities.Publishers
import utilities.Triggers
import utilities.Steps
import utilities.Defaults
import utilities.Wrappers
//import utilities.projects.Project
//import utilities.projects.Repo

import org.yaml.snakeyaml.Yaml
import groovy.io.FileType


//////////////////////////////


/*
 *  jira
 */ 
def jira_release_notes   = ''
def jira_release_version = '1.0.0'

def jira_release_filter = '(Released, Closed)'

/*
 *  credentials
 */
def credentialID = 'abs_ssh'


//////////////////////////////////////////////////////////////////////


def getYaml(file)
{
    def components = file.path.split('/')
    def dir        = components[-2] + '/' + components[-1]

    // .yml file 
    def fileName   = dir
    def fileStream = getClass().getClassLoader().getResourceAsStream(fileName)

    def yaml = new Yaml().load(fileStream);

    return yaml
}


// loop through projects
def ymlFiles = new File("${WORKSPACE}/yml-files/")

ymlFiles.eachFileRecurse (FileType.FILES) { file ->

    def yaml = getYaml(file)

    def projectKey  = yaml.project_key
    def projectName = yaml.project_name

    // folder
    folder(projectName)
    {
        displayName(projectName)
        description("$projectName project")
    }

    def host_http   = yaml.host_http ? yaml.host_http : "${STASH_HTTP_HOST}"
    def host_ssh    = yaml.host_ssh  ? yaml.host_ssh  : "${STASH_SSH_HOST}" 
    def branchNames = yaml.branchNames
    def repoObjects = yaml.repos

    // loop through repositories
    repoObjects.each { repoObject ->
      
        def email_list = repoObject.email_list.join(',')
        def repoName   = repoObject.repo 
        def jobName    = repoName
        def newJob     = job("$projectName/${jobName}")
        
        // base job
        def defaults  = new Defaults()
        def job_label = repoObject.job_label

        defaults.getBaseJob(newJob, job_label, email_list) 
        {
            def scm_schedule = repoObject.scm_schedule ? repoObject.scm_schedule : "${SCM_SCHEDULE}"

            envs = repoObject.envs
            environmentVariables 
            {
                env('PROJECT_NAME', projectName)
                env('PROJECT_KEY', projectKey)
                env('REPO_NAME', repoName)
                env('BRANCH_NAMES', branchNames)
                env('SRVM_CUSTOMER_IDS', envs.SRVM_CUSTOMER_IDS)
                env('SRVM_RELEASE_FOR', envs.SRVM_RELEASE_FOR)
                env('SRVM_RELEASE_BY', envs.SRVM_RELEASE_BY)
                env('SRVM_PRODUCT_CATALOG', envs.SRVM_PRODUCT_CATALOG)
                env('BUILD_PLATFORM', envs.BUILD_PLATFORM)
                env('BUILD_OUTPUT_PATH', envs.BUILD_OUTPUT_PATH)

                keepBuildVariables(true)
            }
            //defaults.setEnvironmentVariables(delegate, projectName, projectKey, repoName, branchNames)


            // wrappers
            //Wrappers.setJiraRelease(delegate, jira_release_notes, jira_project_key, jira_release_version, jira_release_filter)
            Wrappers.setSshAgent(delegate, credentialID)

            // triggers
            Triggers.setTriggers(delegate, scm_schedule)

            // scm: git
            SCM.setSCM(delegate, host_http, host_ssh, projectKey, repoName, branchNames, credentialID)
            
            // build steps
            Steps steps = new Steps()
            steps.setBuildScript(delegate, repoObject.build_command)
            steps.setEnvInjectBuilder(delegate)

            // publishers
            Publishers publishers = new Publishers()
            // -archive
            publishers.setArchiveArtifacts(delegate, '${BUILD_OUTPUT_PATH}/*')
            // -git
            publishers.setGitPublisher(delegate, repoName)
            // -jira
            publishers.setJiraIssue(delegate)
            //publishers.setJiraVersion(delegate, jira_project_key)
            // -srvm
            publishers.setSRVMScript(delegate)
            // -reports
            //publishers.setPublishHtml(delegate, "Screenshots", '${REPORT_PATH}/screenshots.html')
            //publishers.setArchiveJunit(delegate, '${REPORT_PATH}/report.xml')
        }
    }
}