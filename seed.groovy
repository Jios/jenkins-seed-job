import utilities.SCM
import utilities.Publishers
import utilities.Triggers
import utilities.Steps
import utilities.Defaults
import utilities.Wrappers
import utilities.CustomViews

import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.TypeDescription
import org.yaml.snakeyaml.error.YAMLException
import org.yaml.snakeyaml.nodes.Node

import utilities.projects.YamlConstructor
import utilities.projects.Project
import utilities.projects.Repo
import utilities.projects.Environment
import utilities.projects.Jira

import org.yaml.snakeyaml.Yaml
import groovy.io.FileType

import lib.src.main.groovy.*


//////////////////////////////


/*
 *  credentials
 */
def credentialID = 'abs_ssh'


//////////////////////////////////////////////////////////////////////


def getFilePath(file)
{
    def components = file.path.split('/')
    // remove WORKSPACE path
    def file_path  = components[-2] + '/' + components[-1]

    return file_path
}

def getFileStreamFromFilePath(file_path)
{
    def fileStream = getClass().getClassLoader().getResourceAsStream(file_path)

    return fileStream
}

def parseYamlFileWithStream(fileStream)
{
    Constructor     yamlConstr = new YamlConstructor(Project.class);
    TypeDescription typeDesc   = new TypeDescription(Project.class);
    
    typeDesc.putListPropertyType("repos",       Repo.class)
    typeDesc.putListPropertyType("environment", Environment.class)
    typeDesc.putListPropertyType("jira",        Jira.class)

    yamlConstr.addTypeDescription(typeDesc)

    // new Yaml instance with YamlConstructor
    Yaml yaml = new Yaml(yamlConstr);
    
    // parse yaml file to class
    Project project = (Project)yaml.load(fileStream);
    
    return project
}

def exportPropertiesFile(file_path)
{
    def properties = new Properties() 

    streamFileFromWorkspace(file_path).withStream 
    { 
        InputStream it -> properties.load(it) 
    }
}


// loop through projects
def ymlFiles = new File("${WORKSPACE}/yml-files/")

ymlFiles.eachFileRecurse (FileType.FILES) { file ->

    // process yml file only
    if(file.name.endsWith('.yml')) 
    {
        /*
         *
         *  Project: stash/bitbucket project
         *
         */

        def file_path     = getFilePath(file)
        def fileStream    = getFileStreamFromFilePath(file_path)
        def projectObject = parseYamlFileWithStream(fileStream)

        // folder
        folder(projectObject.name)
        {
            displayName(projectObject.name)
            description("$projectObject.name project")
        }

        // set project
        projectObject.setHost_http("${STASH_HTTP_HOST}")
        projectObject.setHost_ssh("${STASH_SSH_HOST}")

        projectObject.repos.each { repoObject ->
          
            /*
             *
             *  Repository: jenkins job
             *
             */

            // set repo
            repoObject.setBranchNames("${BRANCH_NAMES}")
            repoObject.setSchedule("${SCM_SCHEDULE}")
            repoObject.setOutput_path('${BUILD_OUTPUT_PATH}')
            repoObject.setReport_path('${REPORT_PATH}')

            // base job
            def jobName  = "$projectObject.name/${repoObject.name}"
            def newJob   = job(jobName)
            def defaults = new Defaults()

            new Defaults(
                projectObject: projectObject,
                repoObject: repoObject
            ).build(this).with 
            {
                // wrappers
                //Wrappers.setJiraRelease(delegate, jira_release_notes, jira_project_key, jira_release_version, jira_release_filter)
                Wrappers.setSshAgent(delegate, credentialID)

                // triggers
                Triggers.setTriggers(delegate, repoObject.schedule)

                // scm: git
                SCM.setSCM(delegate, projectObject, repoObject, credentialID)
                
                // build steps
                Steps steps = new Steps()
                steps.setBuildScript(delegate, repoObject.build_command)
                steps.setEnvInjectBuilder(delegate)

                // publishers
                Publishers publishers = new Publishers()
                // -archive
                publishers.setArchiveArtifacts(delegate, "$repoObject.output_path/*")
                // -git
                publishers.setGitPublisher(delegate, repoObject.name)
                // -jira
                publishers.setJiraIssue(delegate)
                //publishers.setJiraVersion(delegate, jira_project_key)
                // -srvm
                publishers.setSRVMScript(delegate)
                // -reports
                //publishers.setPublishHtml(delegate, "Screenshots", "$repoObject.report_path/screenshots.html")
                //publishers.setArchiveJunit(delegate, "$repoObject.report_path/report.xml")
            }

            // schedule a job
            //queue(jobName)

            // list view
            listView(projectObject.name) 
            {
                jobs
                {
                    name(jobName)
                }

                recurse(true)
                
                columns 
                {
                    status()
                    weather()
                    name()
                    lastSuccess()
                    lastFailure()
                    lastDuration()
                    buildButton()
                }
            }
        }
    }
}
