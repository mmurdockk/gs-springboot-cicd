pipeline {
    agent {
        label 'jenkins-slave-1'
    }

    tools {
        maven 'MVN 3.5'
        jdk 'JDK 1.8'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '6', artifactNumToKeepStr: '3'))
    }

    stages {
        stage('git checkout') {
            steps {
                deleteDir()
                checkout scm
            }
        }

        stage('Artifactory configuration') {
            steps {
                script {
                    // Obtain an Artifactory server instance, defined in Jenkins --> Manage:
                    server = Artifactory.server 'Artifactory local'

                    rtMaven = Artifactory.newMavenBuild()
                    rtMaven.deployer releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local', server: server
                    rtMaven.resolver releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot', server: server

                    buildInfo = Artifactory.newBuildInfo()
                }
            }

        }

        stage('Maven deploy') {
            steps {
                script {
                    rtMaven.run pom: 'maven-example/pom.xml', goals: 'install', buildInfo: buildInfo
                }
            }

        }

        stage('Publish build info') {
            steps {
                script {
                    server.publishBuildInfo buildInfo
                }
            }
        }
    }

    post {
        always {
            gatlingArchive()
        }
    }
}