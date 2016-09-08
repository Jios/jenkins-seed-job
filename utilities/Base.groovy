package utilities


class Base
{
	static void baseJob(def job, Closure optionalClosure = null)
	{
		job.with
		{

def email_list   = 'jiantutk@gmail.com,jian_li@tutk.com,vita_lin@tutk.com'

/*
 *  jenkins environment variables, e.g. env.JENKINS_URL
 *  https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project
 */
// System.getenv()
def jenkins_url = System.getenv().JENKINS_URL

/*
 *  slack info
 */ 
def slack_token   = 'nLIgN4qKWOKH32e5mXiuKCww'
def slack_team    = 'tutk-kalay'
def slack_channel = '#testing'

/*
 *  log rotator
 */
def ndaysToKeep         = 30
def nToKeep             = 10
def artifactNdaysToKeep = 7
def artifactNToKeep     = 14

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
	        Publishers.setJiraIssueUpdater(it)
	        Publishers.setMailer(it, email_list)
	        Publishers.setSlackNotifier(it, slack_token, slack_team, slack_channel, jenkins_url)
		}

		if(optionalClosure) 
		{
			optionalClosure.delegate = job
			optionalClosure.run()
		}
	}
}
