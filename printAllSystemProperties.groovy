println "===== Jenkins JVM System Properties =====\n"

def props = System.getProperties().sort { a, b -> a.key <=> b.key }

props.each { key, value ->
    println "${key} = ${value}"
}

println "\n===== Proxy-related Properties =====\n"

def proxyKeys = ['http.proxyHost', 'http.proxyPort', 'http.nonProxyHosts',
                 'https.proxyHost', 'https.proxyPort',
                 'ftp.proxyHost', 'ftp.proxyPort',
                 'socksProxyHost', 'socksProxyPort']

proxyKeys.each { key ->
    def val = System.getProperty(key)
    if (val != null) {
        println "${key} = ${val}"
    }
}