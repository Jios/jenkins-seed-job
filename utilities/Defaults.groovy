package utilities

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.*
import javaposse.jobdsl.dsl.jobs.WorkflowJob

class Defaults
{
	def projectObject
	def repoObject
	def name

	/*
	 *  log rotator
	 */
	def ndaysToKeep         = 30
	def nToKeep             = 10
	def artifactNdaysToKeep = 7
	def artifactNToKeep     = 14

	Job build(DslFactory factory) 
	{
        factory.job(name) 
        {
            CommonUtils.addDefaults(delegate, projectObject, repoObject)

        	steps
        	{
                // testing metaClass example: lib/src/main/groovy/echo.groovy
                //echo('test', 123123)
            }
        }
    }


/////////////////////////////////////////////////////////////////////// 


    Job initStage(DslFactory factory) 
	{
        factory.job(name) 
        {
        	deliveryPipelineConfiguration(repoObject.name, 'scm')

            CommonUtils.addDefaults(delegate, projectObject, repoObject)

            Publishers.publishWorkspace(delegate)
            Publishers.setDownstreamJob(delegate, "${name}-build")

            /*
            parameters
            {
                string
                {
                    name('TAG_NAME')
                    defaultValue('')
                    description('git tag name')
                }
            }
            */
        }
    }

    Job buildStage(DslFactory factory) 
	{
        factory.job(name + '-build') 
        {
			deliveryPipelineConfiguration(repoObject.name, 'build')

            CommonUtils.addDefaults(delegate, projectObject, repoObject)

			SCM.cloneUpstreamWorkspace(delegate, name)
			
			Publishers publishers = new Publishers()
            publishers.setArchiveArtifacts(delegate, "$repoObject.output_path/*")

            Publishers.setDownstreamJob(delegate, "${name}-test")
        }
    }

    Job testStage(DslFactory factory) 
	{
        factory.job(name + '-test') 
        {
			deliveryPipelineConfiguration(repoObject.name, 'test')

            CommonUtils.addDefaults(delegate, projectObject, repoObject)

            SCM.cloneUpstreamWorkspace(delegate, name)

            Publishers.setDownstreamJob(delegate, "${name}-deploy")
        }
    }

    Job deployStage(DslFactory factory) 
	{
        factory.job(name + '-deploy') 
        {
			deliveryPipelineConfiguration(repoObject.name, 'deploy')

            CommonUtils.addDefaults(delegate, projectObject, repoObject)

            String upstream     = name + "-build"
            String include_path = repoObject.output_path + "/*"
            Steps.copyArtifactsFromUpstream(delegate, upstream, include_path, '', repoObject.output_path)

            if (repoObject.jira) 
            {
                Publishers.setDownstreamJob(delegate, name + "-jira")
            }
        }
    }

    Job jiraStage(DslFactory factory) 
	{
        factory.job(name + '-jira') 
        {
        	deliveryPipelineConfiguration(repoObject.name, 'jira')

            CommonUtils.addDefaults(delegate, projectObject, repoObject)
        }
    }


/////////////////////////////////////////////////////////////////////// 


    Job buildPipeline(DslFactory factory)
    {
    	def name = "pipeline-$projectObject.name-${repoObject.name}"

    	factory.pipelineJob(name)
    	{
    		CommonUtils.addDefaults(delegate, projectObject, repoObject)

		    definition 
		    {
		        cps 
		        {
		            sandbox()
		            script("""
		            	// https://www.cloudbees.com/blog/top-10-best-practices-jenkins-pipeline-plugin

		            	parallel 'integration-tests':{
						    node('mvn-3.3'){ ... }
						}, 'functional-tests':{
						    node('selenium'){ ... }
						}

						timeout(time:5, unit:'DAYS') {
						    input message:'Approve deployment?', submitter: 'it-ops'
						}

						withEnv(["PATH+MAVEN=${tool 'm3'}/bin"]) {
						    sh 'mvn clean verify'
						}

						stash excludes: 'target/', name: 'source'
						unstash 'source'

		            	stage 'build'
		                node(${repoObject.label}) 
		                {
		                    stage 'Hello world'
		                    echo 'Hello World 1'

		                    stage 'pull source code'
							checkout scm

							stage 'clean up'
							sh 'mvn clean install'
		                    
		                    stage 'invoke another pipeline'
		                    build 'pipeline-being-called'

		                    stage 'Goodbye world'
		                    echo 'Goodbye world'
		                }
		            """.stripIndent())
		        }
		    }
    	}
    }

}
