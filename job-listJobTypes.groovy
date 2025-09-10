// Jenkins Script Console: list job types in use (+ counts & examples)
import jenkins.model.Jenkins
import hudson.model.*
import com.cloudbees.hudson.plugins.folder.Folder
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
import hudson.matrix.MatrixProject
//import hudson.maven.MavenModuleSet
import jenkins.branch.OrganizationFolder

def j = Jenkins.get()

// CONFIG — set to false to skip counting “branch jobs” inside multibranch projects
boolean includeBranchJobs = true
int exampleLimit = 5

def summary = [:].withDefault { [count:0, examples:[]] }
def add = { String type, String fullName ->
    def rec = summary[type]
    rec.count++
    if (rec.examples.size() < exampleLimit) rec.examples << fullName
    summary[type] = rec
}

j.getAllItems(Item.class).each { Item item ->
    String type

    switch (item) {
        case WorkflowMultiBranchProject:
            type = "Multibranch Pipeline (container)"
            break
        case OrganizationFolder:
            type = "Organization Folder (container)"
            break
        case Folder:
            type = "Folder (container)"
            break
        case WorkflowJob:
            WorkflowJob wj = (WorkflowJob) item
            def parent = wj.getParent()
            if (parent instanceof WorkflowMultiBranchProject && !includeBranchJobs) return
            if (parent instanceof WorkflowMultiBranchProject) {
                type = "Pipeline (MB branch)"
            } else {
                def defn = wj.getDefinition()
                if (defn instanceof CpsScmFlowDefinition)      type = "Pipeline (SCM Jenkinsfile)"
                else if (defn instanceof CpsFlowDefinition)     type = "Pipeline (inline Jenkinsfile)"
                else                                           type = "Pipeline (other)"
            }
            break
        case FreeStyleProject:
            type = "Freestyle"
            break
        case MatrixProject:
            type = "Matrix (multi-config)"
            break
        case MavenModuleSet:
            type = "Maven (legacy)"
            break
        case ExternalJob:
            type = "External Job"
            break
        default:
            if (item instanceof Job) {
                type = "Other Job: ${item.getClass().getName()}"
            } else {
                type = "Non-Job Item: ${item.getClass().getName()}"
            }
    }

    add(type, item.getFullName())
}

// Pretty print (sorted by count desc)
println "== Job & Project Types In Use =="
summary.entrySet()
        .sort { -it.value.count }
        .each { e ->
            println String.format("%-35s %6d  examples=%s", e.key, e.value.count, e.value.examples)
        }

// Optional: CSV output (copy below if you want a clean CSV)
println "\nCSV:"
println "type,count,examples"
summary.each { k,v ->
    def ex = v.examples.collect { it.replaceAll(',', ';') }.join('|')
    println "\"${k}\",${v.count},\"${ex}\""
}
