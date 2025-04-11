println "plugins:"
Jenkins.instance.pluginManager.plugins.each { plugin ->
    println " - id: "+ plugin.getShortName()
}