import jenkins.model.*
import hudson.model.*

def thresholdBuilds = 5
Jenkins.instance.getAllItems(Job).each { job ->
    if (job.builds.size() > thresholdBuilds) {
        println "Job: ${job.fullName} has ${job.builds.size()}"
    }
}