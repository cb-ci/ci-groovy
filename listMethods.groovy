// Replace this with how you are obtaining the instance
def remoteDirClass = com.cloudbees.opscenter.context.remote.RemoteDirectory.class

println "=== Methods for ${remoteDirClass.name} ==="
remoteDirClass.methods.each { method ->
    println "${method.returnType.simpleName} ${method.name}(${method.parameterTypes*.simpleName.join(', ')})"
}