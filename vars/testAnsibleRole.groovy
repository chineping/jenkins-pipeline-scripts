#!/usr/bin/groovy

import hudson.model.*
import com.cloudbees.groovy.cps.NonCPS

def call(Closure body=null) {
    this.vars = [:]
    call(vars, body)
}

def call(Map vars, Closure body=null) {

    echo "[JPL] Executing `vars/testAnsibleRole.groovy`"

    vars = vars ?: [:]

    def roleName = vars.get("roleName", "todo")

    call(roleName)

    if (body) {
        body()
    }

}

@NonCPS
def call(String roleName) {

    if (roleName != null && roleName.trim() != "" ) {

       build = sh (
         script: "./scripts/test-with-ara.sh " + roleName + " 2>&1 > molecule-" + roleName + ".log",
         returnStatus: true
       )

       echo "ARA RETURN CODE : ${build}"
       if (build == 0) {
         echo "ARA SUCCESS"
       } else {
         echo "ARA UNSTABLE"
         error 'There are errors in ara'
         currentBuild.result = 'UNSTABLE'
       }

       junit testResults: "**/ara-" + roleName + ".xml", healthScaleFactor: 2.0, allowEmptyResults: true, keepLongStdio: true, testDataPublishers: [[$class: 'ClaimTestDataPublisher']]

       archiveArtifacts artifacts: "molecule-" + roleName + ".log", onlyIfSuccessful: false, allowEmptyArchive: true

       publishHTML([
         allowMissing: false,
         alwaysLinkToLastBuild: false,
         keepAll: true,
         reportDir: "./ara-" + roleName + "/",
         reportFiles: 'index.html',
         includes: '**/*',
         reportName: "ARA " + roleName + " Report",
         reportTitles: "ARA " + roleName + " Report Index"
       ])

    }

}
