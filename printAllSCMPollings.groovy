import hudson.triggers.*;
import org.jenkinsci.plugins.workflow.job.*;
//import hudson.maven.MavenModuleSet;
println("--- SCM Polling for WorkflowJob jobs ---");
List<WorkflowJob> WorkflowJobList = Jenkins.getInstance().getAllItems(WorkflowJob.class);
for (WorkflowJob workflowJob : WorkflowJobList) {
    SCMTrigger scmTrigger = workflowJob.getSCMTrigger();
    if (scmTrigger!= null) {
        String spec = scmTrigger.getSpec();
        if (spec != null) {
            println(workflowJob.getFullName() + " with spec " + spec);
        }
    }
}

println("--- SCM Polling for FreeStyle jobs ---");
List<FreeStyleProject> freeStyleProjectList = Jenkins.getInstance().getAllItems(FreeStyleProject.class);
for (FreeStyleProject freeStyleProject : freeStyleProjectList) {
    SCMTrigger scmTrigger = freeStyleProject.getSCMTrigger();
    if (scmTrigger!= null) {
        String spec = scmTrigger.getSpec();
        if (spec != null) {
            println(freeStyleProject.getFullName() + " with spec " + spec);
        }
    }
}
