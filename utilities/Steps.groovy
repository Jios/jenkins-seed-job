package utilities

class Steps
{
	void setBuildScript (def context, sh_command)
	{
		String properties_file = '${WORKSPACE}/properties/prebuild.properties'
		
    	context.steps 
        {
        	envInjectBuilder 
			{
				propertiesFilePath(properties_file)
				propertiesContent('')
			}
            shell(sh_command)
        }
	}

	void setEnvInjectForPostBuild(def context, properties_file='${WORKSPACE}/properties/postbuild.properties')
	{
		context.steps 
		{
			envInjectBuilder 
			{
				propertiesFilePath(properties_file)
				propertiesContent('')
			}
		}
	}

	static void preparePropertiesFiles(def context, sh_script='')
	{
    	context.steps 
        {
        	shell(sh_script)
        }
	}

	static void copyArtifactsFromUpstream(def context, job_name, include, exclude, targetPath)
	{
		context.steps
		{
			copyArtifacts(job_name) 
			{
	            includePatterns(include)
	            excludePatterns(exclude)
	            targetDirectory(targetPath)
	            fingerprintArtifacts()
	            flatten()
	            optional()
	            buildSelector 
	            {
	                latestSuccessful(true)
	            }
	        }
		}
	}
}