import jenkins.model.*
import hudson.model.*
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
import org.jenkinsci.plugins.workflow.job.WorkflowJob

// Define the threshold (7 days ago)
def sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)

// Get all jobs in Jenkins
def allJobs = Jenkins.instance.getAllItems(Job)

// Initialize counts
def activeFreestyleJobs = 0
def activePipelineJobs = 0
def activeMultibranchJobs = 0
def jobsMBURLs= [ : ]

allJobs.each { job ->
    def lastBuild = job.getLastBuild()
    if (lastBuild != null && lastBuild.getTimeInMillis() >= sevenDaysAgo) {
        if (job instanceof FreeStyleProject) {
            activeFreestyleJobs++
        } else if (job instanceof WorkflowJob) {
            if (job.getParent() instanceof WorkflowMultiBranchProject) {
                activeMultibranchJobs++ // Correctly identifies multibranch jobs
                //println "${job.fullName} → ${job.absoluteUrl}"
                jobsMBURLs.put("${job.fullName}", "${job.absoluteUrl}")
            } else {
                activePipelineJobs++
            }
        }
    }
}

// Print results
println "Active Jobs (Executed in the Last 7 Days):"
println "Freestyle Jobs: ${activeFreestyleJobs}"
println "Pipeline Jobs: ${activePipelineJobs}"
println "Multibranch Jobs: ${activeMultibranchJobs}"
println "Total Active Jobs: ${activeFreestyleJobs + activePipelineJobs + activeMultibranchJobs}"
println "Multibranch Jobs executed since the last 7 Days"
jobsMBURLs.each { key, value ->
    println "Name: $key, URL: $value"
}