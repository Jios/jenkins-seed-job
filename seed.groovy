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
 *  bash commands
 */ 
def sh_python    = 'python srvm/srvm.py'
def sh_build     = 'bash build.sh'

/*
 *  slack info
 */ 
def slack_channel = '#testing'

/*
 *  jira
 */ 
def jira_release_notes   = ''
def jira_release_version = '1.0.0'

def jira_release_filter = '(Released, Closed)'

/*
 *  directories
 */ 
def output_path = 'output'
def report_path = "reports"

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

    def projectKey  = yaml['project_key']
    def projectName = yaml['project_name']

    // folder
    folder(projectName)
    {
        displayName(projectName)
        description("$projectName project")
    }

    def host_http   = yaml['host_http']
    def host_ssh    = yaml['host_ssh']
    def branchNames = yaml['branchNames']
    def repoObjects = yaml['repos']

    // loop through repositories
    repoObjects.each { repoObject ->
      
        def email_list = repoObject['email_list'].join(',')
        def repoName   = repoObject['repo'] 
        def jobName    = repoName
        def newJob     = job("$projectName/${jobName}")
        
        // base job
        def defaults  = new Defaults()
        def job_label = repoObject['job_label']

        defaults.getBaseJob(newJob, job_label, slack_channel, email_list) 
        {
            def scm_schedule = repoObject['scm_schedule']

            defaults.setEnvironmentVariables(delegate, projectName, projectKey, repoName, branchNames)


            // wrappers
            //Wrappers.setJiraRelease(delegate, jira_release_notes, jira_project_key, jira_release_version, jira_release_filter)
            Wrappers.setSshAgent(delegate, credentialID)

            // triggers
            Triggers.setTriggers(delegate, scm_schedule)

            // scm: git
            SCM.setSCM(delegate, host_http, host_ssh, projectKey, repoName, branchNames, credentialID)
            
            // build steps
            Steps steps = new Steps()
            steps.setBuildScript(delegate, sh_build)
            steps.setEnvInjectBuilder(delegate)

            // publishers
            Publishers publishers = new Publishers()
            // -archive
            publishers.setArchiveArtifacts(delegate, "${output_path}/*")
            // -git
            publishers.setGitPublisher(delegate, repoName)
            // -jira
            publishers.setJiraIssue(delegate)
            //publishers.setJiraVersion(delegate, jira_project_key)
            // -srvm
            publishers.setSRVMScript(delegate)
            // -reports
            //publishers.setPublishHtml(delegate, "Screenshots", "${report_path}/screenshots.html")
            //publishers.setArchiveJunit(delegate, "${report_path}/report.xml")
        }
    }
}