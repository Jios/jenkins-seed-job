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


// credentials
def credentialID = 'abs_ssh'


//////////////////////////////////////////////////////////////////////


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

def getJobName(projectObject, repoObject)
{
    def jobName  = "$projectObject.name/${repoObject.name}"

    return jobName
}

def setProject(projectObject)
{
    projectObject.setHost_http("${STASH_HTTP_HOST}")
    projectObject.setHost_ssh("${STASH_SSH_HOST}")
}

def setRepo(repoObject)
{
    repoObject.setBranchNames("${BRANCH_NAMES}")
    repoObject.setSchedule("${SCM_SCHEDULE}")
}


//////////////////////////////////////////////////////////////////////


def ymlFiles = new File("${WORKSPACE}/yamlFiles/")

ymlFiles.traverse(type: FileType.FILES, nameFilter: ~/.*yml$/) { file ->

    /**
     *  Project: stash/bitbucket project
     */

    def file_path     = Project.getFilePath(file)
    def fileStream    = getFileStreamFromFilePath(file_path)
    def projectObject = parseYamlFileWithStream(fileStream)

    setProject(projectObject)

    /**
     *  folder
     */ 

    folder(projectObject.name)
    {
        displayName(projectObject.name)
        description("$projectObject.name project")
    }

    /**
     *  list view
     */

    new CustomViews(
        viewName: projectObject.name
    ).createListView(this).with 
    {
        projectObject.repos.each { repoObject ->
            jobs
            {
                def jobName = getJobName(projectObject, repoObject)
                names(jobName,             \
                      jobName + '-build',  \
                      jobName + '-test',   \
                      jobName + '-deploy', \
                      jobName + '-jira')
            }
        }
    }

    new CustomViews(
        viewName: 'DPV ' + projectObject.name
    ).createDeliverPipelineView(this).with 
    {
        pipelines
        {
            //pipelines{regex(/$projectObject.name\/-/)}
            projectObject.repos.each { repoObject ->

                def jobName = getJobName(projectObject, repoObject)
                component(repoObject.name, jobName)
            }
        }
    }


    projectObject.repos.each { repoObject ->
      
        setRepo(repoObject)

        /**
         *  Repository: jenkins job
         */

        def jobName = getJobName(projectObject, repoObject)

        new Defaults(
            projectObject: projectObject,
            repoObject: repoObject,
            name: jobName
        ).initStage(this).with 
        {
            Wrappers.setSshAgent(delegate, credentialID)

            Triggers.setTriggers(delegate, '@daily')
            Triggers.setTriggers(delegate, repoObject.schedule)

            SCM.setSCM(delegate, projectObject, repoObject, credentialID)

            Steps.preparePropertiesFiles(delegate)
        }

        new Defaults(
            projectObject: projectObject,
            repoObject: repoObject,
            name: jobName
        ).buildStage(this).with 
        {
            // build steps
            Steps steps = new Steps()
            steps.setBuildScript(delegate, repoObject.build_command)
            steps.setEnvInjectBuilder(delegate)

            // publishers
            Publishers publishers = new Publishers()
            publishers.setGitPublisher(delegate, repoObject.name)
        }

        new Defaults(
            projectObject: projectObject,
            repoObject: repoObject,
            name: jobName
        ).testStage(this).with 
        {
            Publishers publishers = new Publishers()

            publishers.setPublishHtml(delegate, "Screenshots", "$repoObject.report_path/screenshots.html")
            publishers.setArchiveJunit(delegate, "$repoObject.report_path/report.xml")
        }
        
        new Defaults(
            projectObject: projectObject,
            repoObject: repoObject,
            name: jobName
        ).deployStage(this).with 
        {
            Publishers publishers = new Publishers()
            publishers.setSRVMScript(delegate)

            if (repoObject.jira) 
            {
                new Defaults(
                    projectObject: projectObject,
                    repoObject: repoObject,
                    name: jobName
                ).jiraStage(this).with 
                {
                    Wrappers.setJiraRelease(delegate, repoObject.jira)

                    publishers.setJiraVersion(delegate, repoObject.jira.key)
                }
            }
        }

        // schedule a job
        //queue(jobName + '-scm')
    }
}

