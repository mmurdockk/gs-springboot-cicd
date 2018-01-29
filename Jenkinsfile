pipeline {
    /*
    agent {
        label 'jenkins-slave-1'
    }*/
    agent any

    parameters {
        password(description: 'SSH Password ?', name: 'SSH_PASSWORD')
    }

    tools {
        jdk 'JDK 1.8'
        maven 'MVN 3.5'
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
                    rtMaven.tool = 'MVN 3.5'
                    rtMaven.deployer releaseRepo: 'libs-release-local', snapshotRepo: 'libs-snapshot-local', server: server
                    rtMaven.resolver releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot', server: server

                    buildInfo = Artifactory.newBuildInfo()
                    buildInfo.env.capture = true
                }
            }

        }

        stage('Maven deploy') {
            steps {
                script {
                    rtMaven.run pom: 'pom.xml', goals: 'clean package', buildInfo: buildInfo
                }
            }

            post {
                success {
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
        }

        stage('Deploy artifact to Artifactory') {
            steps {
                script {
                    rtMaven.deployer.deployArtifacts buildInfo
                    server.publishBuildInfo buildInfo
                }
            }
        }

        stage('Deploy to server with SSH') {
            steps {
                script {
                    println "$currentBuild.projectName/$currentBuild.number"

                    // Create the download spec.

                    // Instead of LATEST in Artifactory OSS $currentBuild.number
                    def downloadSpec = """{
                        "files": [
                            {
                                "pattern": "libs-snapshot-local/org/springframework/gs-springboot-cicd/0.1.0-SNAPSHOT/*",
                                "build": "$currentBuild.projectName/$currentBuild.number",
                                "flat": "true",
                                "target": "artifactory/gs-springboot-cicd.jar"
                            }
                        ]
                    }"""

                    // Download from Artifactory.
                    server.download spec: downloadSpec

                    configFileProvider([configFile(fileId: 'f4afadcf-c2f0-4aa2-950d-b7776f40c97f', targetLocation: 'build.xml')]) {
                        withAnt(installation: 'Ant 1.9.6', jdk: 'JDK 1.8') {
                            if (isUnix()) {
                                sh 'echo "ANT_HOME: ${ANT_HOME}"'
                                sh 'ant ssh-exec -Dssh.simple.mode=false -Dssh.userid=adrien -Dssh.host=40.68.116.191 -Dssh.command=ls -Dssh.fail-on-error=true -Dssh.password=${SSH_PASSWORD}'
                            } else {
                                bat 'echo "ANT_HOME: %ANT_HOME%"'
                                bat 'ant ssh-exec -Dssh.simple.mode=false -Dssh.userid=adrien -Dssh.host=40.68.116.191 -Dssh.command="if [ -e /etc/init.d/myapp ]; then sudo /etc/init.d/myapp stop; fi " -Dssh.fail-on-error=true -Dssh.password="%SSH_PASSWORD%"'
                                bat 'ant ssh-exec -Dssh.simple.mode=false -Dssh.userid=adrien -Dssh.host=40.68.116.191 -Dssh.command="rm -rf deploy/*" -Dssh.fail-on-error=true -Dssh.password="%SSH_PASSWORD%"'
                                bat 'ant scp-put -Dssh.simple.mode=false -Dssh.userid=adrien -Dssh.host=40.68.116.191 -Dssh.remote.dir=deploy -Dssh.fileset.dir=artifactory -Dssh.fileset.dir.includes=*.jar -Dssh.password="%SSH_PASSWORD%"'
                                bat 'ant ssh-exec -Dssh.simple.mode=false -Dssh.userid=adrien -Dssh.host=40.68.116.191 -Dssh.command="chmod 500 deploy/*.jar" -Dssh.fail-on-error=true -Dssh.password="%SSH_PASSWORD%"'
                                bat 'ant ssh-exec -Dssh.simple.mode=false -Dssh.userid=adrien -Dssh.host=40.68.116.191 -Dssh.command="sudo /etc/init.d/myapp start" -Dssh.fail-on-error=true -Dssh.password="%SSH_PASSWORD%"'
                            }
                        }
                    }
                }
            }
        }

        stage('Gatling') {
            steps {
                script {
                    rtMaven.run pom: 'pom.xml', goals: 'gatling:test'
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