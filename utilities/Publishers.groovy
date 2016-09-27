package utilities

class Publishers
{
    void setJiraIssueUpdater(def context)
    {
    	context.publishers
    	{
        	jiraIssueUpdater()
        }
    }

    void setJiraIssue(def context)
    {
    	def jira_project_key = 'ABS'
		def jira_assignee    = 'jian_li'

    	context.publishers
    	{
	        createJiraIssue 
	        {
	            projectKey(jira_project_key)
	            testDescription('Jenkins job build failed.\n  Job url: ${JOB_URL}\n  Build url: ${BUILD_URL}')
	            assignee(jira_assignee)
	            component('')
	        }
	    }
    }

    static void setJiraVersion(def context, jira_project_key)
    {
    	context.publishers
    	{
        	createJiraVersion 
	        {
	          projectKey(jira_project_key)
	          version('${iOS_APP_VERSION}_${BUILD_NUMBER}')
	        }
	    }
    }

    void setMailer(def context, email_list)
    {
    	context.publishers
    	{
    		mailer(email_list, false, true)
		}
    }

    static void setPublishHtml(def context, name, html_path)
    {
    	context.publishers
    	{
    		publishHtml
	        {
				report(html_path) 
				{
					reportName(name)
					keepAll()
					allowMissing()
					alwaysLinkToLastBuild()
				}
	        }
		}
    }

    void setArchiveArtifacts(def context, path_pattern)
    {
    	context.publishers
    	{
    		archiveArtifacts
	        {
	          	pattern(path_pattern)
	        	onlyIfSuccessful()
	        }
		}
    }

    static void setArchiveJunit(def context, xml_path)
    {
    	context.publishers
    	{
    		archiveJunit(xml_path)
    		{
	        	allowEmptyResults()
	        }
		}
    }

    static void setSRVMScript(def context, sh_script='')
    {
    	context.publishers
    	{
	        postBuildScripts 
	        {
				steps 
				{
					shell(sh_script)
				}
				onlyIfBuildSucceeds(true)
	        }

		}
    }

    void setSlackNotifier(def context)
    {
    	context.publishers
    	{
			slackNotifier
			{
				teamDomain('')
				authToken('')
				buildServerUrl('')
				room('{SLACK_CHANNEL}')
				startNotification(true)
				notifySuccess(true)
				notifyAborted(true)
				notifyNotBuilt(true)
				notifyUnstable(true)
				notifyFailure(true)
				notifyBackToNormal(true)
				notifyRepeatedFailure(true)
				includeTestSummary(true)
				commitInfoChoice('AUTHORS_AND_TITLES')
				includeCustomMessage(false)
				customMessage('')
				sendAs('ABS')
			}
		}
    }

    static void setGitPublisher(def context, repoName)
    {
    	context.publishers
    	{
    		gitPublisher 
    		{
    			pushOnlyIfSuccess(true)
    			pushMerge(true)
    			forcePush(false)

				// Specify tags to push at the completion of the build.
				def name = 'jenkins/${branch}/${BUILD_NUMBER}'
	            def msg  = 'tag from Jenkins, ${NODE_NAME}:${JOB_NAME} ${BUILD_URL}'

				tagsToPush 
				{
					tagToPush 
					{
						targetRepoName(repoName)
						tagName(name)
						tagMessage(msg)
						createTag(true)
						updateTag(true)
					}
				}
			}
    	}
    }

    void setBrokenBuildClaiming(def context)
    {
    	context.publishers
    	{
    		allowBrokenBuildClaiming()
    	}
    }

    static void setDownstreamJob(def context, job_name)
    {
    	context.publishers
    	{
    		downstream(job_name, 'SUCCESS')
    	}
    }

    static void publishWorkspace(def context)
    {
    	context.publishers 
    	{
	        publishCloneWorkspace('**')
	        {
	        	archiveMethod('ZIP')
	        	overrideDefaultExcludes(true)
	        	criteria('Successful')
	        }
	    }
    }

    static void collectDownstreamTestResults(def context)
    {
    	context.publishers
    	{
    		aggregateDownstreamTestResults()
    	}
    }
}
