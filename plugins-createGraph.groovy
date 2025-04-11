#! /groovy

//To produce a graph, execute this snippet to generate a DOT graph (graphviz) file...
// see https://stackoverflow.com/questions/37757231/list-jenkins-plugins-and-dependencies-with-graph

/**
 * Steps:
 * run this scrip in script console
 * copy the result to an file (e.g. plugins.txt)
 * run one of these commands to get the visual graph
 * dot -Tsvg  plugins.txt > plugins.svg
 * dot -Tpng  plugins.txt > plugins.png
 *
 */
def plugins = jenkins.model.Jenkins.instance.getPluginManager().getPlugins()
println "digraph test {"
plugins.each {
    def plugin = it.getShortName()
    println "\"${plugin}\";"
    def deps =  it.getDependencies()
    deps.each {
        def s = it.shortName
        println "\"${plugin}\" -> \"${s}\";"
    }
}
println "}"