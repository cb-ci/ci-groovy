import com.cloudbees.hudson.plugins.folder.Folder
import com.cloudbees.hudson.plugins.folder.AbstractFolder
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject
String[] jobsToKeepCos = ["folderpath/jobsname1",
                          "folderpath/jobsname2"]
List issuesList=new ArrayList()
List deleteList=new ArrayList()
List keepList=new ArrayList()
//Jenkins.instance.getAllItems(AbstractItem.class).each {
Jenkins.instance.getAllItems(org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject).each {item ->
    //println item.fullName + " - " + item.class
    //if (! jobsToKeepCos.contains(item.fullName)) {
    if (! jobsToKeepCos.any { it.equalsIgnoreCase(item.fullName) }) {
        try {
            //item.delete()
            //sleep(1000)
            deleteList.add(item.fullName + "," + item.class)

        } catch (Exception e){
            issuesList.add(item.fullName + "," +  item.class)
        }

    }else {
        //println "job will NOT be deleted: " + item.fullName + "class: " + item.class
        //keepList.add(item.fullName + "," + item.class)
        keepList.add(item.fullName )
    }
};

println "####################"
println "keep List"
keepList.each{
    println it
}
println "####################"
println "delete List"
deleteList.each{
    println it
}
println "####################"
println "issues List"
issuesList.each{
    println it
}



