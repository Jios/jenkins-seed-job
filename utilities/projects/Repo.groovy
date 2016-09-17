package utilities.projects

import utilities.projects.Environment


class Repo
{
	public String name
	public String label	// slave label
	public String schedule = null
	public String build_command
	public List<String> branchNames = null
	public List<String> email_list
	public Environment environment

	Repo()
	{
		
	}

	Repo(java.util.LinkedHashMap map)
	{
		
	}


	// Getter
	String getEmail_list() 
	{ 
		def emails = email_list.join(',')
		return emails
	}


	// Setter
	void setBranchNames(String branches) 
	{ 
		this.branchNames = branchNames ? branchNames : Eval.me(branches)
	}

	void setSchedule(String scm_schedule)
	{
		this.schedule = schedule ? schedule : scm_schedule
	}
}