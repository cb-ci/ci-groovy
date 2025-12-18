import jenkins.model.Jenkins
import hudson.model.Job
import hudson.model.Run

// Configuration: Time window in minutes
long timeWindowMinutes = 60
long timeWindowMillis = timeWindowMinutes * 60 * 1000
long now = System.currentTimeMillis()
int buildCount = 0

// Iterate over all jobs and count builds in the time window
Jenkins.instance.getAllItems(Job.class).each { job ->
    // We only check builds that ended recently
    // (start time + duration) could be used, but start time is usually sufficient for rate estimation
    job.getBuilds().byTimestamp(now - timeWindowMillis, now).each { build ->
        buildCount++
    }
}

double rate = buildCount / timeWindowMinutes

println "------------------------------------------------"
println "Builds in last ${timeWindowMinutes} minutes: ${buildCount}"
println String.format("Build Rate: %.4f builds/minute", rate)
println "------------------------------------------------"