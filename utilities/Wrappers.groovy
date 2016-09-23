package utilities
//import javaposse.jobdsl.dsl.Job

class Wrappers
{
	static void setColorizeOutput(def context)
	{
		context.wrappers 
		{
			colorizeOutput('xterm')
			timestamps()
		}
	}

	static void setJiraRelease(def context, jiraObject)
	{
		context.wrappers 
		{
			generateJiraReleaseNotes 
			{
				projectKey(jiraObject.key)
				environmentVariable(jiraObject.release_notes)
				release(jiraObject.release_version)
				filter("status in ${jiraObject.release_filter}")
			}
		}
	}

	static void setSshAgent(def context, credential_id)
	{
		context.wrappers
		{
			sshAgent(credential_id)
		}
	}
}