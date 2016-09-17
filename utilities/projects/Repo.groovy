package utilities.projects

import utilities.projects.Environment


class Repo
{
	public String name
	public String job_label
	public String scm_schedule
	public String build_command
	public List<String> branchNames
	public List<String> email_list
	public Environment environment

	Repo()
	{
		
	}

	Repo(java.util.LinkedHashMap map)
	{
		
	}
}