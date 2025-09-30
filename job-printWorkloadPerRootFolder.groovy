
/*
  Jenkins Script Console — Build activity by root or team folder → JSON & CSV

  CONFIG:
    PERIOD = '14d'         // 'Nd' days, 'Nh' hours, 'Nm' minutes
    INCLUDE_ROOT = false   // include jobs not in any folder under "(root)"
    SHOW_EMPTY = true      // include folders with 0 builds in period
    OUTPUT = 'both'        // 'json' | 'csv' | 'both'
    WRITE_FILES = false    // also write userContent/*.json and *.csv for download

  OUTPUT:
    - Prints JSON and/or CSV to the console.
    - If WRITE_FILES=true, writes:
        $JENKINS_HOME/userContent/build-activity-<timestamp>.json
        $JENKINS_HOME/userContent/build-activity-<timestamp>.csv
*/

import jenkins.model.Jenkins
import hudson.model.Job
import hudson.model.Item
import com.cloudbees.hudson.plugins.folder.AbstractFolder
import groovy.json.JsonOutput
import java.math.RoundingMode

// ---------- config ----------
final String  PERIOD       = '75d'
final boolean INCLUDE_ROOT = true
final boolean SHOW_EMPTY   = true
final String  OUTPUT       = 'both'   // 'json' | 'csv' | 'both'
final boolean WRITE_FILES  = true
// ----------------------------

long parseMillis(String s) {
    def m = (s =~ /(?i)^\s*(\d+)\s*([dhm])\s*$/)
    assert m, "PERIOD must look like '7d', '24h', or '90m' (got: ${s})"
    long n = m[0][1] as long
    switch (m[0][2].toLowerCase()) {
        case 'd': return n * 24L * 60L * 60L * 1000L
        case 'h': return n * 60L * 60L * 1000L
        case 'm': return n * 60L * 1000L
    }
    throw new IllegalArgumentException("Unsupported PERIOD unit in ${s}")
}

String topLevelFolderName(Item item) {
    def p = item?.getParent()
    AbstractFolder top = null
    while (p && !(p instanceof Jenkins)) {
        if (p instanceof AbstractFolder) top = (AbstractFolder)p
        p = p.getParent()
    }
    return top?.getName()
}

def j = Jenkins.instance
long now = System.currentTimeMillis()
long since = now - parseMillis(PERIOD)

// Collect counts per top-level folder
def counts = new LinkedHashMap<String, Long>().withDefault { 0L }
j.getItems(AbstractFolder).each { f -> counts[f.name] = 0L }  // ensure all top folders appear
if (INCLUDE_ROOT) counts['(root)'] = 0L

j.getAllItems(Job.class).each { Job job ->
    String top = topLevelFolderName(job)
    if (top == null && !INCLUDE_ROOT) return
    String key = top ?: '(root)'
    def runList = job.getBuilds()
    int n = runList ? runList.byTimestamp(since, now).size() : 0
    if (n > 0 || SHOW_EMPTY) {
        counts[key] = (counts[key] ?: 0L) + n
    }
}

// Build rows + shares
long total = (counts.values().findAll { it != null }.sum() ?: 0L) as long

def rows = counts.collect { k, v ->
    long builds = (v ?: 0L) as long
    double share = total ? (builds * 100.0d / total) : 0.0d
    double share1 = Math.round(share * 10d) / 10d
    [team: k, builds: builds, sharePercent: share1]
}.findAll { SHOW_EMPTY || it.builds > 0 }
        .sort { -it.builds }

// JSON payload
def payload = [
        period      : [label: PERIOD, since: new Date(since), until: new Date(now)],
        totalBuilds : total,
        folders     : rows
]

// CSV text
String csv = (['team','builds','share_percent'] + rows.collect { r ->
    // escape commas/quotes in team names
    def team = r.team.contains(',') || r.team.contains('"') ? '"' + r.team.replace('"','""') + '"' : r.team
    "${team},${r.builds},${String.format(java.util.Locale.US, '%.1f', r.sharePercent)}"
}).join('\n')

// Print as requested
if (OUTPUT in ['json','both']) {
    println "=== JSON ==="
    println JsonOutput.prettyPrint(JsonOutput.toJson(payload))
    println ""
}
if (OUTPUT in ['csv','both']) {
    println "=== CSV ==="
    println "team,builds,share_percent"
    rows.each { r ->
        def team = r.team.contains(',') || r.team.contains('"') ? '"' + r.team.replace('"','""') + '"' : r.team
        println "${team},${r.builds},${String.format(java.util.Locale.US, '%.1f', r.sharePercent)}"
    }
    println ""
}

// Optionally write files into userContent for download
if (WRITE_FILES) {
    def ucDir = new File(j.getRootDir(), "userContent")
    ucDir.mkdirs()
    String base = "build-activity-${System.currentTimeMillis()}"
    def jsonFile = new File(ucDir, "${base}.json")
    def csvFile  = new File(ucDir, "${base}.csv")
    jsonFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(payload)) + "\n"
    csvFile.text  = "team,builds,share_percent\n" + rows.collect { r ->
        def team = r.team.contains(',') || r.team.contains('"') ? '"' + r.team.replace('"','""') + '"' : r.team
        "${team},${r.builds},${String.format(java.util.Locale.US, '%.1f', r.sharePercent)}"
    }.join('\n') + "\n"

    def rootUrl = j.getRootUrl()
    println "Wrote files to: ${jsonFile.absolutePath} and ${csvFile.absolutePath}"
    if (rootUrl) {
        println "Download URLs:"
        println "  ${rootUrl}userContent/${jsonFile.name}"
        println "  ${rootUrl}userContent/${csvFile.name}"
    } else {
        println "(Root URL not set; set Jenkins URL in Configure System to get clickable links.)"
    }
}
