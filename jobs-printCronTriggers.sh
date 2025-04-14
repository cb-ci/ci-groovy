Jenkins.instance.getAllItems(Job).each { job ->
  def triggers = job.getTriggers()
    triggers.each { trigger ->
      if (trigger.value instanceof hudson.triggers.TimerTrigger) {
        println "Job: ${job.fullName} | Cron: ${trigger.value.spec}"
    }
  }
}