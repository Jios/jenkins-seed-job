package utilities

class Defaults
{
	/*
	 *  log rotator
	 */
	def ndaysToKeep         = 30
	def nToKeep             = 10
	def artifactNdaysToKeep = 7
	def artifactNToKeep     = 14

	void setBaseJob(def job, projectObject, repoObject, Closure optionalClosure = null)
	{
		job.with
		{
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

}
