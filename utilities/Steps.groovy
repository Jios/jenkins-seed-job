package utilities

class Steps
{
	void setBuildScript (def context, sh_command)
	{
		String properties_file = '${WORKSPACE}/properties/prebuild.properties'
		
		def sh_script = '''git checkout ${GIT_BRANCH}
						  |
						  |mkdir -p ${WORKSPACE}/properties 
						  |touch ${WORKSPACE}/properties/prebuild.properties
						  |touch ${WORKSPACE}/properties/postbuild.properties 
						  |'''.stripMargin()
    	context.steps 
        {
        	shell(sh_script)
        	envInjectBuilder 
			{
				propertiesFilePath(properties_file)
				propertiesContent('')
			}
            shell(sh_command)
        }
	}

	void setEnvInjectBuilder(def context)
	{
		String properties_file = '${WORKSPACE}/properties/postbuild.properties'

		context.steps 
		{
			envInjectBuilder 
			{
				propertiesFilePath(properties_file)
				propertiesContent('')
			}
		}
	}

	void copyArtifactsFromUpstream(def context, include, exclude, targetPath)
	{
		context.steps
		{
			copyArtifacts('upstream') 
			{
	            includePatterns(include)
	            excludePatterns(exclude)
	            targetDirectory(targetPath)
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