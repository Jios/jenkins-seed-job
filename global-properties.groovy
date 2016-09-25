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

def getWorkspace()
{
	def build    = Thread.currentThread().executable
	def workspace = build.workspace.toString()

	return workspace
}

// http://stackoverflow.com/questions/12747946/how-to-write-and-read-a-file-with-a-hashmap
void save_map_to_properties_file(map, filename) 
{
	def path = getWorkspace() + '/properties'
	new File(path).mkdir()

	def filepath  = path + "/" + filename


	Properties properties = new Properties();
	properties.putAll(map);

/*
	for (Map.Entry<String,String> entry : map.entrySet()) 
	{
	    properties.put(entry.getKey(), entry.getValue());
	}
	*/

	properties.store(new FileOutputStream(filepath), null);
}

Map load_map_from_properties_file(filepath)
{
	Map<String, String> map = new HashMap<String, String>();
	Properties properties   = new Properties();

	properties.load(new FileInputStream(filepath));

	map = new HashMap<Object, Object>(properties)
/*
	for (String key : properties.stringPropertyNames()) 
	{
	   map.put(key, properties.get(key).toString());
	}
	*/

	return map
}


/////////////////////////////////////////////////////////////////////////////////////////////


// oauth test token: https://api.slack.com/docs/oauth-test-tokens
def slack_test_token = "xoxp-17652790182-83055396336-83062265957-e531214efd33104a2f384406a5c4f3db"
def slack_jenkins_token = "nLIgN4qKWOKH32e5mXiuKCww"
def slack_fastlane_token = "https://hooks.slack.com/services/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"

def git_http_host = "http://stash.tutk.com:7990"
def git_ssh_host  = "ssh://git@stash.tutk.com:7999"

// Organizations: TUTK RD1
def crashlytics_api_key      = "a903f72a6ffdd06539302df50101d05b48d830ea"
def crashlytics_build_secret = "039b2c50ea29ef03f71f5cb93b598bfdfe4a19a23054f66cdddd13275803e599"

def branch_names = '''['*/master',
					  |'*/develop',
					  |'*/release',
					  |'*/release/*',
					  |'*/feature/*',
					  |'*/hotfix/*',
					  |'*/bugfix/*']'''.stripMargin().replaceAll('\n', '')

// env variable map
def envMap = [:]
envMap = ["SLACK_PYTHON_TOKEN": slack_test_token,
		  "SLACK_JENKINS_TOKEN": slack_jenkins_token,
		  "SLACK_FASTLANE_TOKEN": slack_fastlane_token,
		  "SLACK_TEAM": "tutk-kalay",
		  "SLACK_CHANNEL": "#build",
		  "JIRA_HOST": "http://jira.tutk.com",
		  "CONFLUENCE_HOST": "http://confluence.tutk.com/",
		  "STASH_HTTP_HOST": git_http_host,
		  "STASH_SSH_HOST": git_ssh_host,
		  "BITBUCKET_HTTP_HOST": git_http_host,
		  "BITBUCKET_SSH_HOST": git_ssh_host,
		  "BRANCH_NAMES": branch_names,
		  "CRASHLYTICS_API_TOKEN": crashlytics_api_key,
		  "CRASHLYTICS_BUILD_SECRET": crashlytics_build_secret,
		  "SCM_SCHEDULE": "@daily",
		  "PREBUILD_PROPERTIES_PATH": "properties/prebuild.properties",
		  "POSTBUILD_PROPERTIES_PATH": "properties/postbuild.properties",
		  "BUILD_OUTPUT_FILENAME": "",
		  "BUILD_OUTPUT_PATH": "output",
		  "REPORT_PATH": "reports"]


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

/// properties
//
save_map_to_properties_file(envMap, "envPro.properties)



