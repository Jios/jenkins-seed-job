package utilities.projects

import utilities.projects.Repo


class Project
{
	public String host_http
	public String host_ssh
	public String key
	public String name
	public List<Repo> repos

	// constructor
	Project()
	{
		
	}

	Project(java.util.LinkedHashMap map)
	{
		
	}

	// Getter
	String getKey() { return key }

	String getSshUrl()
	{
		def url = host_ssh + '/' + key

		return url.toLowerCase()
	}

	String getHttpUrl()
	{
		def url = host_http + '/projects/' + key

		return url.toLowerCase()
	}

	// Setter
	void setHost_http(String host) 
	{ 
		this.host_http = host_http ? host_http : host
	}

	void setHost_ssh(String host) 
	{ 
		this.host_ssh = host_ssh ? host_ssh: host
	}
}