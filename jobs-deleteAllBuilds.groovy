#! /groovy
//see also
// https://docs.cloudbees.com/docs/cloudbees-ci-kb/latest/client-and-managed-controllers/groovy-to-list-all-jobs
// https://docs.cloudbees.com/docs/cloudbees-ci-kb/latest/client-and-managed-controllers/how-can-i-purge-or-clean-the-build-queue
// https://stackoverflow.com/questions/40307037/how-to-get-a-list-of-running-jenkins-builds-via-groovy-script
runningBuilds = Jenkins.instance.getView('All').getBuilds().findAll() { it.getResult().equals(null) }
Jenkins.instance.queue.clear()
Jenkins.instance.getView('All').getBuilds().findAll() { it.getResult().equals(null);it.stop()  }
Jenkins.instance.getView('All').getBuilds().findAll() { it.getResult().equals(null);it.delete()  }
