package utilities

//import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.View
import javaposse.jobdsl.dsl.ViewFactory
import javaposse.jobdsl.dsl.*


class CustomViews
{
	def viewName

	View createListView(ViewFactory factory)
	{
		factory.listView(viewName) 
		{
			recurse(true)

            columns 
            {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
                lastDuration()
                buildButton()
            }
		}
	}
}