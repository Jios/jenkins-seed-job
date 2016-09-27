package utilities
import lib.src.main.groovy.echo

class Steps
{
	void setBuildScript (def context, sh_command)
	{
		// stage: build
    	context.steps 
        {
            shell(sh_command)
        }
	}

	void setEnvInjectForPreBuild(def context, properties_file='properties/prebuild.properties')
	{
		// stage: build
    	context.steps 
        {
        	envInjectBuilder 
			{
				propertiesFilePath(properties_file)
				propertiesContent('')
			}
        }
	}

	void setEnvInjectForPostBuild(def context, properties_file='properties/postbuild.properties')
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
		// stage: scm
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