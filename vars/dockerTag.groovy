#!/usr/bin/groovy

def call(def DOCKER_TAG, def commit = "", def dbms = "") {

    // create safe tag for docker image from given parameters
    def branchSafeName = env.BRANCH_NAME.replaceAll("/", "-").replaceAll("%2F", "-")

    if (env.BRANCH_NAME ==~ /PR-.*|feature\/.*|bugfix\/.*/ ) {
        DOCKER_TAG = ("temp-${branchSafeName}-${env.BUILD_ID}").toLowerCase()
    } else if (env.BRANCH_NAME ==~ /develop/ ) {
        DOCKER_TAG = ("develop").toLowerCase()
    } else if (env.BRANCH_NAME ==~ /master.*/ ) {
        DOCKER_TAG = ("${branchSafeName}").toLowerCase()
    } else if (env.BRANCH_NAME ==~ /release\/.+/ ) {
        DOCKER_TAG = ("${branchSafeName}").toLowerCase()
    }

    if (commit != null && commit.trim() != "" ) {
        def commitShortSHA1 = commit.take(7)
        if (dbms == null || dbms.trim() == "" ) {
            DOCKER_TAG += "-${commitShortSHA1}"
        } else {
            DOCKER_TAG += "-${commitShortSHA1}-${dbms}"
        }
    }

    return DOCKER_TAG.toLowerCase()

}
