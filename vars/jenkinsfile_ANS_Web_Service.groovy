import org.ans.Constants

def call() {
    def SonarStatus = ''
def buildMailRequire = ''
def deploymentMailRequire = ''
def buildTeamsNotificationRequire = ''
def deploymentTeamsNotificationRequire = ''
def serverName = ''
def deployFileName = ''
def jenkinsFolderPath =''
def deploymentServerDetails = ''
def deploymentServerPwd = ''
def attachedLog=true

    pipeline {
        agent any

        tools {
            // Install the Maven version configured as "M3" and add it to the path.
            jdk "JDK"
            maven "Maven"
        }
        parameters {
            string(name: 'BranchName', defaultValue: '' , description: '')
        }
        stages {
            
            stage('checkout'){  // Require only first time or anychange happened.
                when {expression {"${env.BUILD_NUMBER}" == 1} }
                steps {
                     git url: "https://github.com/capitawrld/${env.JOB_NAME}.git",  credentialsId: 'newID'
                   
                }
            }
            stage("Build Project")
            {
                steps{
// 			switch("${params.BranchName}".toUpperCase()){
//                             case 'QA' :
//                                 serverName = Constants.ANS_QA_BRANCH_SERVERNAME
//                                 break;
//                             case 'RELEASE' :
//                                 serverName = Constants.ANS_RELEASE_BRANCH_SERVERNAME
//                                 break;
//                             case 'MASTER' :
//                                 serverName = Constants.ANS_MASTER_BRANCH_SERVERNAME
//                                break;
//                         }
			sh "mvn -Dmaven.repo.local=/var/lib/jenkins/repository/PRODUCTION clean package install"
                    // sh "mvn -Dmaven.test.failure.ignore=true clean package install"
			// sh "mvn -Dmaven.repo.local=/var/lib/jenkins/repository/" + serverName + " clean package install"
                    //sh "mvn -Dmaven.repo.local=/var/lib/jenkins/repository/" + "${env.BRANCH_NAME}".toUpperCase() + " clean package install" This is not require bcoz of master branch
                    //sh "mvn -Dmaven.repo.local=/var/lib/jenkins/repository/" + serverName + " clean package install"
                }
                post {
                    // If Maven was able to run the tests, even if some of the test
                    // failed, record the test results and archive the jar file.
                    success {
                        //junit '**/target/surefire-reports/TEST-*.xml'
                        archiveArtifacts 'target/*.jar'

                        echo "Branch Name ---> ${params.BranchName}"
                        
                        script{
                            
                            attachedLog = false
                            
                        switch("${params.BranchName}".toUpperCase()){
                            case 'QA' :
                                serverName = Constants.ANS_QA_BRANCH_SERVERNAME
                                buildMailRequire  = Constants.ANS_QA_BUILD_MAIL_REQUIRE
                                deploymentMailRequire  = Constants.ANS_QA_DEPLOY_MAIL_REQUIRE
                                buildTeamsNotificationRequire  = Constants.ANS_QA_BUILD_TEAMS_NOTIFICATION_REQUIRE
			                    deploymentTeamsNotificationRequire  = Constants.ANS_QA_DEPLOY_TEAMS_NOTIFICATION_REQUIRE
                                deployFileName = Constants.ANS_QA_DEPLOY_FILENAME
                                jenkinsFolderPath = Constants.ANS_QA_SERVER_JENKINPATH
                                deploymentServerDetails = ANS_QA_Server
                                deploymentServerPwd = ANS_QA_Pwd
                                    
                                break;
                            case 'RELEASE' :
                                serverName = Constants.ANS_RELEASE_BRANCH_SERVERNAME
                                buildMailRequire = Constants.ANS_UAT_BUILD_MAIL_REQUIRE
                                deploymentMailRequire = Constants.ANS_UAT_DEPLOY_MAIL_REQUIRE
                                buildTeamsNotificationRequire  = Constants.ANS_UAT_BUILD_TEAMS_NOTIFICATION_REQUIRE
			                    deploymentTeamsNotificationRequire  = Constants.ANS_UAT_DEPLOY_TEAMS_NOTIFICATION_REQUIRE
                                deployFileName = Constants.ANS_UAT_DEPLOY_FILENAME
                                jenkinsFolderPath = Constants.ANS_UAT_SERVER_JENKINPATH
                                deploymentServerDetails = ANS_UAT_Server
                                deploymentServerPwd = ANS_UAT_Pwd
                                break;
                            case 'MASTER' :
                                serverName = Constants.ANS_MASTER_BRANCH_SERVERNAME
                                buildMailRequire = Constants.ANS_PROD_BUILD_MAIL_REQUIRE
                                deploymentMailRequire = Constants.ANS_PROD_DEPLOY_MAIL_REQUIRE
                                buildTeamsNotificationRequire  = Constants.ANS_PROD_BUILD_TEAMS_NOTIFICATION_REQUIRE
			                    deploymentTeamsNotificationRequire  = Constants.ANS_PROD_DEPLOY_TEAMS_NOTIFICATION_REQUIRE
                                deployFileName = Constants.ANS_PROD_DEPLOY_FILENAME
                                jenkinsFolderPath = Constants.ANS_PROD_SERVER_JENKINPATH
                                deploymentServerDetails = ANS_PROD_Server
                                deploymentServerPwd = ANS_PROD_Pwd
                                break;
                        }
                        }
                    }
                    always
                    {
                        echo "Build successfully"
                         script {
                            if(buildMailRequire){
                                //mail bcc: '', body: '' + MailBody('buildMailBody'), cc: Constants.ANS_EMAILTO_CC, charset: 'UTF-8', from: 'jnk.pipeline@onlinepsbloans.com', mimeType: 'text/html', replyTo: '', subject: "(Web Project) Build Status ${currentBuild.currentResult}: Project name -> ${env.JOB_NAME}", to: Constants.ANS_EMAILTO    // from email id is not mandatroy
                                emailext attachLog: '' + attachedLog, body: '' + MailBody('buildMailBody'), from: 'jnk.pipeline@onlinepsbloans.com', mimeType: 'text/html', replyTo: '', subject: "Build Status ${currentBuild.currentResult}: Project name -> ${env.JOB_NAME}", to: Constants.ANS_EMAILTO + ",cc:" + Constants.ANS_EMAILTO_CC  // emailext used to send attachment, simple mail not able to send attachment.
                            }
                        }
                    }
                   
    	
                }
            }
            
            stage("Depolyment on Server")
            {
                steps{
                    sh "scp /var/lib/jenkins/jobs/${env.JOB_NAME}/builds/${env.BUILD_NUMBER}/archive/target/*.jar " + deploymentServerDetails + ":" + jenkinsFolderPath 
                    sh "ssh -tt " + deploymentServerDetails + " 'echo "+ deploymentServerPwd +" | sudo -S sh " + jenkinsFolderPath + "/" + deployFileName + " ${env.JOB_NAME} ' &"
                }
                post{
                    always
                    {
                        script{
                            if (deploymentMailRequire){
                                mail bcc: '', body: '' + MailBody('deploymentMailBody','',serverName), cc:  Constants.ANS_EMAILTO_CC, charset: 'UTF-8', from: 'jnk.pipeline@onlinepsbloans.com', mimeType: 'text/html', replyTo: '', subject: "${currentBuild.currentResult} : Deployment for ${env.JOB_NAME} on " + serverName + " server is ${currentBuild.currentResult}" , to: Constants.ANS_EMAILTO
                            }
                            if(deploymentTeamsNotificationRequire){
                                office365ConnectorSend webhookUrl: Constants.ANS_TEAMS_NOTIFICATION_WEBHOOK,
                                message: MailBody('deploymentMailBody','',SonarStatus),
                                status: "Deployment ${currentBuild.currentResult}",
                                remarks: ""
                            }
                        }
                        
                        
                    }
                }
            }
        }
    }
}
