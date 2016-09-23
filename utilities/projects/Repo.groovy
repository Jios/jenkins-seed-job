package utilities.projects

import utilities.projects.Environment
import utilities.projects.Jira


class Repo
{
	public String name
	public String label	// slave label
	public String schedule = null
	public String build_command
	public String report_path = null
	public String output_path = null
	public List<String> branchNames = null
	public List<String> email_list
	public Environment environment
	public Jira        jira

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

	String getSshUrl(projectUrl)
	{
		def url = projectUrl + '/' + name + '.git'

		return url.toLowerCase()
	}

	String getHttpUrl(projectUrl)
	{
		def url = projectUrl + '/repos/' + name

		return url.toLowerCase()
	}

	String getReport_path()
	{
		this.report_path = report_path ? report_path : '${REPORT_PATH}'
	}

	String getOutput_path()
	{
		this.output_path = output_path ? output_path : '${BUILD_OUTPUT_PATH}'
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