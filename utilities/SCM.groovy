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
	static void setSCM(def context, projectObject, repoObject, credentialsID)
	{
		context.scm 
		{
			git
			{
				remote
				{
					name(repoObject.name)
					url("${projectObject.host_ssh}/${projectObject.key}/${repoObject.name}.git")
					credentials(credentialsID)
				}

				repoObject.branchNames.each { branchName ->
					branch(branchName)
				}

				browser 
				{
					stash("${projectObject.host_http}/projects/${projectObject.key}/repos/${repoObject.name}")
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