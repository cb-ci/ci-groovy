// Groovy script for CloudBees CI / Jenkins script console
// Lists all Pipeline jobs (WorkflowJob) that are currently waiting on an `input` step.
// Safe to run on a Managed Controller 2.528.2.x; read‑only, no modifications.

import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.jenkinsci.plugins.workflow.support.steps.input.InputAction
import org.jenkinsci.plugins.workflow.support.steps.input.InputStepExecution

Jenkins jenkins = Jenkins.get()

def results = []

jenkins.getAllItems(WorkflowJob).each { WorkflowJob job ->
    job.builds.each { WorkflowRun build ->
        InputAction ia = build.getAction(InputAction)
        if (ia == null) {
            return // no input steps recorded on this run
        }

        boolean waiting = false
        try {
            waiting = ia.isWaitingForInput()
        } catch (Throwable t) {
            println "[WARN] Could not check isWaitingForInput() for ${build}: ${t.class.simpleName}: ${t.message}"
        }

        if (!waiting) {
            return
        }

        List<InputStepExecution> executions = []
        try {
            executions = ia.getExecutions()
        } catch (Throwable t) {
            println "[WARN] Could not getExecutions() for ${build}: ${t.class.simpleName}: ${t.message}"
        }

        if (!executions) {
            return
        }

        String rootUrl = jenkins.rootUrl ?: ""
        String buildUrl = rootUrl + build.getUrl()
        String inputBaseUrl = buildUrl + ia.getUrlName() + "/"  // typically .../job/<job>/<build>/input/

        executions.each { InputStepExecution exec ->
            def input = exec.getInput()

            // Collect a structured record for pretty printing later
            results << [
                    jobFullName     : job.fullName,
                    jobDisplayName  : job.fullDisplayName,
                    buildNumber     : build.number,
                    buildDisplayName: build.displayName,
                    buildUrl        : buildUrl,
                    inputId         : exec.id,
                    inputMessage    : input?.message,
                    inputOk         : input?.ok,
                    inputSubmitter  : input?.submitter,
                    inputUrl        : inputBaseUrl + exec.id + "/"
            ]
        }
    }
}

if (results.isEmpty()) {
    println "No Pipeline runs are currently waiting for an input step."
    return
}

println "=== Pipelines currently waiting on an input step ===".toString()
println "Found ${results.size()} pending input step(s).".toString()
println ""

results.each { r ->
    println "Job       : ${r.jobDisplayName} (${r.jobFullName})"
    println "Build     : ${r.buildDisplayName} (#${r.buildNumber})"
    println "Build URL : ${r.buildUrl}"
    println "Input ID  : ${r.inputId}"
    println "Message   : ${r.inputMessage ?: '-'}"
    println "OK label  : ${r.inputOk ?: 'Proceed'}"
    println "Submitter : ${r.inputSubmitter ?: 'Anyone with permission'}"
    println "Input URL : ${r.inputUrl}"
    println "" // blank line between entries
}
