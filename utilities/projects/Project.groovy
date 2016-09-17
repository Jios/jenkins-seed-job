package utilities.projects

import utilities.projects.Repo


class Project
{
	public String host_http
	public String host_ssh
	public String project_key
	public String project_name
	public List<Repo> repos

	// constructor
	Project()
	{
		
	}

	Project(java.util.LinkedHashMap map)
	{
		
	}

	// Getter
	String getHost() { return host }
	String getProject_key() { return project_key }

	// Setter
	void setHost(String host) { this.host = host }
	void setProject_key(String project_key) { this.project_key = project_key }
}