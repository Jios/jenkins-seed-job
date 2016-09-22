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
					def sshUrl = repoObject.getSshUrl(projectObject.getSshUrl())
					url(sshUrl)

					name(repoObject.name)

					credentials(credentialsID)
				}

				repoObject.branchNames.each { branchName ->
					branch(branchName)
				}

				browser 
				{
					def httpUrl = repoObject.getHttpUrl(projectObject.getHttpUrl())
					stash(httpUrl)
				}

				extensions
				{
					//cleanBeforeCheckout()
					cleanAfterCheckout()
					
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