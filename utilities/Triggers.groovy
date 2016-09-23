package utilities

class Triggers
{
    static void setTriggers(def context, def schedule)
    {
	    ///
	    /// build trigger
	    ///
	    context.triggers 
        {
			scm(schedule)
	    }
	
    }
}
