package utilities


//////////////////////////////////////////////////////////////////////

/*
    http://stash.tutk.com:7990/projects/ABS/
    http://stash.tutk.com:7990/projects/ABS/repos/jenkins-ansible/browse
    http://abs@stash.tutk.com:7990/scm/abs/jenkins-ansible.git
    ssh://git@stash.tutk.com:7999/abs/jenkins-ansible.git
*/

//////////////////////////////////////////////////////////////////////


class SCM
{
	static void setSCM(def context, host_http, host_ssh, projectKey, repoName, branchNames, credentialsID)
	{
		context.scm 
		{
			git
			{
				remote
				{
					name(repoName)
					url("${host_ssh}/${projectKey}/${repoName}.git")
					credentials(credentialsID)
				}

				branchNames.each { branchName ->
					branch(branchName)
				}

				browser 
				{
					stash("${host_http}/projects/${projectKey}/repos/${repoName}")
				}

				extensions
				{
					cleanBeforeCheckout()
					
					submoduleOptions 
					{

						// Disables submodules processing.
						disable false
						// Retrieves all submodules recursively.
						recursive true
						// Retrieves the tip of the configured branch in .gitmodules.
						tracking false
					}
				}
			}
		}
	}
}