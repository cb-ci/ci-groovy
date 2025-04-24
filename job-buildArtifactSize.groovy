import jenkins.model.*
import hudson.model.*
import java.text.DecimalFormat

def humanReadableSize(bytes) {
    def units = ["B", "KB", "MB", "GB", "TB"]
    if (bytes == 0) return "0 B"
    int unitIndex = (int) (Math.log10(bytes) / Math.log10(1024))
    double size = bytes / Math.pow(1024, unitIndex)
    return new DecimalFormat("#,##0.#").format(size) + " " + units[unitIndex]
}

Jenkins.instance.getAllItems(Job.class).each { job ->
    job.getBuilds().each { build ->
        def artifactsDir = build.getArtifactsDir()
        if (artifactsDir?.exists() && artifactsDir.listFiles()?.size() > 0) {
            def size = artifactsDir.directorySize()
            println "Job: ${job.fullName} | Build: #${build.number} | Artifact Size: ${humanReadableSize(size)}"
        }
    }
}