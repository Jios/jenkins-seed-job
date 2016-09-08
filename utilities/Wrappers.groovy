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

	static void setJiraRelease(def context, notes, project_key, release_version, release_filter)
	{
		context.wrappers 
		{
			///
			/// jira release notes
			///
			generateJiraReleaseNotes 
			{
				environmentVariable(notes)
				projectKey(project_key)
				release(release_version)
				filter("status in ${release_filter}")
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