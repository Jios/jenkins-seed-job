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

	void getBaseJob(def job, repoObject, Closure optionalClosure = null)
	{
		job.with
		{
			if (repoObject.label != "")
            {
                // slave machnine label
                label(repoObject.label)
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
	        publishers.setMailer(it, repoObject.getEmail_list())
	        publishers.setSlackNotifier(it)
		}

		if(optionalClosure) 
		{
			optionalClosure.delegate = job
			optionalClosure.run()
		}
	}


	void setEnvironmentVariables(def context, projectObject, repoObject)
	{
	    context.environmentVariables 
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
	}
}
