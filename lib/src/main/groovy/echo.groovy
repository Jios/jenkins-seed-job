package lib.src.main.groovy

import javaposse.jobdsl.dsl.helpers.step.*

// from https://github.com/lexandro/jenkins_lib/blob/master/src/main/groovy/echo.groovy

/*
 *
 *  Jenkins DSL plugin tutorials
 *  http://codeventor.blogspot.tw/p/tutorials.html
 *
 */

/*
 * Simple PoC method that take a string and a number and add and echo shell command as job step.
 *
 * Example:
 *   job('jobName') {
 *     steps {
 *       echo('some text', 123)
 *     }
 *   }
 */
StepContext.metaClass.echo = { String strng, Long nmbr ->
    shell('echo string:' + strng + ', number:' + nmbr)
}
