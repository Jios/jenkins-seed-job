package utilities

class Steps
{
	def properties_file = 'postbuild.properties'
	
	void setBuildScript (def context, sh_command)
	{
    	context.steps 
        {
            shell(sh_command)
        }
	}

	void setEnvInjectBuilder(def context)
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
}