import jenkins.model.*
import hudson.model.*

def thresholdBuilds = 5  // keep only the most recent 5 builds

Jenkins.instance.getAllItems(Job).each { job ->
    def builds = job.getBuilds()
    if (builds.size() > thresholdBuilds) {
        println "Cleaning Job: ${job.fullName} (total builds: ${builds.size()})"

        // keep the most recent X builds, delete the rest
        def buildsToDelete = builds.drop(thresholdBuilds)  // drop keeps the latest ones
        buildsToDelete.each { build ->
            try {
                println " -> Deleting build #${build.number} of job ${job.fullName}"
                build.delete()
            } catch (Exception e) {
                println " !! Failed to delete build #${build.number} of job ${job.fullName}: ${e.message}"
            }
        }
    }
}
