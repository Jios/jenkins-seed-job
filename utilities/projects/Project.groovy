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

	// Setter
	void setHost_http(String host) 
	{ 
		this.host_http = host_http ? host_http : host
	}

	void setHost_ssh(String host) 
	{ 
		this.host_ssh= host_ssh? host_ssh: host
	}
}