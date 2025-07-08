import jenkins.model.*
import hudson.model.*
import java.util.Date

def thresholdDays = 1
def cutoffTime = System.currentTimeMillis() - (thresholdDays * 24 * 60 * 60 * 1000L)

Jenkins.instance.getAllItems(Job).each { job ->
    def oldBuilds = job.builds.findAll { it.timeInMillis < cutoffTime }
    if (oldBuilds.size > 0){
       println "Job: ${job.fullName} has ${oldBuilds.size()} builds older than ${thresholdDays} days."
   }
}
