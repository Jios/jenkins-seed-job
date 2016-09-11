import hudson.model.Hudson
import hudson.slaves.EnvironmentVariablesNodeProperty

// reference: http://ryanalberts.com/206/hudson-modify-environment-variables-properties/

def set_env(prop, map)
{	
	if (prop == null) 
	{
		return
	}

	map.each{ var_name, value -> 

		//def token = prop.envVars.get("SLACK_TOKEN", '')
		//println "existing slack token: " + token

		prop.envVars.put(var_name, value)
	}
}

def set_props(props, map)
{
	if (props == null)
	{
		return
	}

	for (prop in props)
	{
		set_env(prop, map)
	}
}


/////////////////////////////////////////////////////////////////////////////////////////////


// oauth test token: https://api.slack.com/docs/oauth-test-tokens
def slack_test_token = "xoxp-15730259699-15737121729-17130785383-885938a11d"
def slack_jenkins_token = "nLIgN4qKWOKH32e5mXiuKCww"
def slack_fastlane_token = "https://hooks.slack.com/services/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"

// env variable map
def envMap = [:]
envMap = ["SLACK_PYTHON_TOKEN": slack_test_token,
		  "SLACK_JENKINS_TOKEN": slack_jenkins_token,
		  "SLACK_FASTLANE_TOKEN": slack_fastlane_token,
		  "SLACK_CHANNEL": "#build"
		  "BITBUCKET_HTTP_HOST": "http://stash.tutk.com:7990",
		  "BITBUCKET_SSH_HOST": "ssh://git@stash.tutk.com:7999"]


/////////////////////////////////////////////////////////////////////////////////////////////


/// master
//
def master = Hudson.instance
def master_props = master.globalNodeProperties.getAll(EnvironmentVariablesNodeProperty.class)

master_props.each { prop ->
	set_env(prop, envMap)
}

set_props(master_props, envMap)

//This is needed in order to persist the change
master.save()


/// slaves
//
def slaves = Hudson.instance.getNodes()
for (slave in slaves) 
{
    println "-----------------------"
    println "slave name: " + slave.name
    println "-----------------------"

    def slave_props = slave.nodeProperties.getAll(EnvironmentVariablesNodeProperty.class)

    set_props(slave_props, envMap)

    slave.save()
}

