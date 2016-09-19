package utilities

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.*
import javaposse.jobdsl.dsl.jobs.WorkflowJob

class Defaults
{
	def projectObject
	def repoObject

	/*
	 *  log rotator
	 */
	def ndaysToKeep         = 30
	def nToKeep             = 10
	def artifactNdaysToKeep = 7
	def artifactNToKeep     = 14

	Job build(DslFactory factory) 
	{
		def name = "$projectObject.name/${repoObject.name}"

        factory.job(name) 
        {
            CommonUtils.addDefaults(delegate)

        	steps
        	{
                // testing metaClass example: lib/src/main/groovy/echo.groovy
                //echo('test', 123123)
            }

        	if (repoObject.label != "")
            {
                // slave machnine label
                label(repoObject.label)
            }

            environmentVariables 
		    {
		        def envObject = repoObject.environment

		        env('PROJECT_NAME',         projectObject.name)
		        env('PROJECT_KEY',          projectObject.key)
		        env('REPO_NAME',            repoObject.name)
		        env('BRANCH_NAMES',         repoObject.branchNames)
		        env('SRVM_CUSTOMER_IDS',    envObject.SRVM_CUSTOMER_IDS)
		        env('SRVM_RELEASE_FOR',     envObject.SRVM_RELEASE_FOR)
		        env('SRVM_RELEASE_BY',      envObject.SRVM_RELEASE_BY)
		        env('SRVM_PRODUCT_CATALOG', envObject.SRVM_PRODUCT_CATALOG)
		        env('BUILD_PLATFORM',       envObject.BUILD_PLATFORM)
		        env('BUILD_OUTPUT_PATH',    envObject.BUILD_OUTPUT_PATH)
		    }

		    // publisher
	        Publishers publishers = new Publishers()
	        publishers.setJiraIssueUpdater(it)
	        publishers.setMailer(it, repoObject.getEmail_list())
	        publishers.setSlackNotifier(it)
        }
    }

    Job buildPipeline(DslFactory factory)
    {
    	def name = "pipeline-$projectObject.name-${repoObject.name}"

    	factory.pipelineJob(name)
    	{
    		CommonUtils.addDefaults(delegate)

		    definition 
		    {
		        cps 
		        {
		            sandbox()
		            script("""
		                node(${repoObject.label}) {
		                    stage 'Hello world'
		                    echo 'Hello World 1'
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
