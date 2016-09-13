package utilities

class Defaults
{
	/*
	 *  jenkins environment variables, e.g. env.JENKINS_URL
	 *  https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project
	 */
	// System.getenv()
	def jenkins_url = System.getenv().JENKINS_URL

	/*
	 *  log rotator
	 */
	def ndaysToKeep         = 30
	def nToKeep             = 10
	def artifactNdaysToKeep = 7
	def artifactNToKeep     = 14

	void getBaseJob(def job, job_label, email_list, Closure optionalClosure = null)
	{
		job.with
		{
			if (job_label != "")
            {
                // slave machnine label
                label(job_label)
            }

			logRotator
	        {
	            daysToKeep(ndaysToKeep)
	            numToKeep(nToKeep)
	            artifactDaysToKeep(artifactNdaysToKeep)
	            artifactNumToKeep (artifactNToKeep)
	        }

	        // wrappers
	        Wrappers.setColorizeOutput(it)

	        // publisher
	        Publishers publishers = new Publishers()
	        publishers.setJiraIssueUpdater(it)
	        publishers.setMailer(it, email_list)
	        publishers.setSlackNotifier(it)
		}

		if(optionalClosure) 
		{
			optionalClosure.delegate = job
			optionalClosure.run()
		}
	}


	void setEnvironmentVariables(def context, projectName, projectKey, repoName, branchNames)
	{
	    context.wrappers
	    {
	    	def shell_script = '''mkdir -p ${WORKSPACE}/properties 
	    						 |touch ${WORKSPACE}/properties/prebuild.properties
	    						 |touch ${WORKSPACE}/properties/postbuild.properties 
	    						 |'''.stripMargin()
	    	
	    	environmentVariables 
	    	{
	    		script(shell_script)
	    		propertiesFile('${WORKSPACE}/properties/prebuild.properties')
		        env('PROJECT_NAME', projectName)
		        env('PROJECT_KEY', projectKey)
		        env('REPO_NAME', repoName)
		        env('BRANCH_NAMES', branchNames)
		        env('DEFAULT_BRANCH_NAME', 'master')
	    	}
	    }
	}
}
