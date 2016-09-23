package utilities

class Steps
{
	void setBuildScript (def context, sh_command)
	{
		String properties_file = '${WORKSPACE}/properties/prebuild.properties'
		
		def sh_script = '''mkdir -p ${WORKSPACE}/properties 
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