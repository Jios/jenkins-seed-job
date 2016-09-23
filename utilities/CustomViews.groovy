package utilities

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

    View createDeliverPipelineView(ViewFactory factory)
    {
        factory.deliveryPipelineView(viewName)
        {
            pipelineInstances(5)
            showAggregatedPipeline()
            columns(2)
            sorting(Sorting.TITLE)
            updateInterval(60)
            enableManualTriggers()
            showAvatars()
            showChangeLog()
            pipelines 
            {
                component('android', 'KalayHome2.0/Android_SourceCode-scm')
                component('ios', 'iOS_Vtech/smarthome-baseapp-scm')
                //regex(/compile-subsystem-(.*)/)
            }
        }
    }
}