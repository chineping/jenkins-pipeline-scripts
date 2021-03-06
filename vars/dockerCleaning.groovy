#!/usr/bin/groovy
import hudson.model.*

def call(def DOCKER_BUILD_IMG = "", def DOCKER_TAG = "latest") {
  try {
    if (DOCKER_BUILD_IMG?.trim()) {
      sh '''docker rmi -f "${DOCKER_BUILD_IMG}:${DOCKER_TAG}"'''
    }
  } catch(exc) {
    echo 'Warn: There was a problem removing image from local docker repo. '+exc.toString()
  }
  try {
    sh '''docker container prune -f --filter until=20m'''
    sh '''docker image prune -f --filter until=20m'''
    sh '''docker network prune -f --filter until=20m'''
  }
  catch(exc) {
    echo 'Warn: There was a problem Cleaning local docker repo. '+exc.toString()
  }
  try {
    sh '''docker system prune --volumes --force'''
  }
  catch(exc) {
    echo 'Warn: There was a problem Cleaning local docker volumes. '+exc.toString()
  }
}
