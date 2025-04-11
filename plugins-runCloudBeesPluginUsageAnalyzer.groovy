#! /groovy

analyzerWork = PeriodicWork.all().get(com.cloudbees.jenkins.plugins.pluginusage.v2.AnalyzerWork.class)
analyzerWork.doRun()