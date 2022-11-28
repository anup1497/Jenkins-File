import org.ans.Constants

def call() {
	
def SonarClear = true
def SonarDashboardURL = ''
def SonarStatus = Constants.SonarStatus.ByPassed
def BuildSuccessfully = false

def mainJobParts = JOB_NAME.tokenize('/') as String[]  // Multibranch project return project name and branch togather in JOB_NAME so to remove branch name I forced to do this. Generally JOB_NAME will be enough to get project name if it is simple project.
def mainJobName = mainJobParts[0]

def buildMailRequire = ''
def deploymentMailRequire = ''
def buildTeamsNotificationRequire = ''
def deploymentTeamsNotificationRequire = ''
def serverName = ''
def deployFileName = ''
def jenkinsFolderPath =''
def deploymentServerDetails = ''
def deploymentServerPwd = ''
def base_service_name = ''
def base_url = ''
def attachedLog=true

    switch(mainJobName.toUpperCase()){
        case 'TN-WEB-BANKER' :
            base_service_name = "service-tn-banker"
            base_url = "/"
            break;
	
	 case 'TN-WEB-ADMIN-PANEL' :
            base_service_name = "service-tn-admin-panel"
            base_url = "/admin-web/"
            break;
	 
	    case 'TN-WEB-BORROWER' :
            base_service_name = "service-tn-borrower"
            base_url = "/borrower/"
            break;
    }

	switch(BRANCH_NAME.toUpperCase()){
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
	

    pipeline {
        agent any
	    //triggers { cron('H/5 * * * *') }
	    triggers {pollSCM('H/5 * * * *')}
        tools {
            // Install the Maven version configured as "M3" and add it to the path.
            nodejs "NodeJSInstaller"
        }

        stages {
            //stage('checkout'){
            //    steps {
            //        git url: "${env.ANS_GIT_URL}/${env.JOB_NAME}.git", branch: "${env.BRANCH_NAME}", credentialsId: 'newID'
                
            //    }
            //}
            stage ("SonarQube analysis") {
                when {
                    expression {"${env.BRANCH_NAME}".toUpperCase() == Constants.ANS_SONAR_PERFORM_BRANCH.toUpperCase() && Constants.ANS_SONAR_TEST_PERFORM && ServicePerformSonar_ANS(mainJobName)}
                }  
                options {
                    timeout(time: 5, unit: 'MINUTES')
                    retry(2)
                }
                environment {
                        SCANNER_HOME = tool 'MySonarScanner'
                }
                steps {
                    script {
                        STAGE_NAME = "SonarQube analysis"
                
                        withSonarQubeEnv('SonarQubeServer') {
                            sh "${SCANNER_HOME}/bin/sonar-scanner"
                            //sh "mvn clean package sonar:sonar"  // This wont consider sonar-project.properties file
                            def props = readProperties  file: '.scannerwork/report-task.txt'
                            SonarDashboardURL =  props['dashboardUrl']
                        }
            
                        //echo "ANS_SONAR_TEST_PERFORM : " + Constants.ANS_SONAR_TEST_PERFORM
                        //echo "ANS_SONAR_FAILURE_MAIL_REQUIRE : " + Constants.ANS_SONAR_FAILURE_MAIL_REQUIRE
                        waitForQualityGate abortPipeline: true

                    }
                }
        
                post {
                    failure {
                            script{
                                if (Constants.ANS_SONAR_FAILURE_MAIL_REQUIRE){
                                    mail bcc: '', body: '' + MailBody('sonarFailureMailBody', SonarDashboardURL), cc: Constants.ANS_EMAILTO_CC, charset: 'UTF-8', from: 'jnk.pipeline@onlinepsbloans.com', mimeType: 'text/html', replyTo: '', subject: 'ABORTED : Sonar Testing Fail', to: Constants.ANS_EMAILTO   // from email id not require 
                                }
                            }
                    }
			success {
				script{
					SonarStatus = Constants.SonarStatus.Passed
				}
			}
                }                               
            }

            stage("Build Web Project")
            {
		    
                steps{
		   			
                    sh "npm install"
                    sh "ng build --base-href=" + base_url + " --prod=true --build-optimizer=true --aot=true"
                }
                post {
                    // If Maven was able to run the tests, even if some of the test
                    // failed, record the test results and archive the jar file.
                    success {
                        //junit '**/target/surefire-reports/TEST-*.xml'
                        //archiveArtifacts artifacts:'**/*.jar', fingerprint: true
                        script{
                            BuildSuccessfully = true
				
			    attachedLog = false
                        }
                    }
                    always
                    {
                        script {
                            if(buildMailRequire){
                            //if ("${ANS_Build_Mail_Require}"=="Yes")
                            //mail bcc: '', body: '' + MailBody('buildMailBody'), cc: Constants.ANS_EMAILTO_CC, charset: 'UTF-8', from: 'jnk.pipeline@onlinepsbloans.com', mimeType: 'text/html', replyTo: '', subject: "(Web Project) Build Status ${currentBuild.currentResult}: Project name -> ${env.JOB_NAME}", to: Constants.ANS_EMAILTO
			    emailext attachLog: attachedLog, body: '' + MailBody('buildMailBody','','',SonarStatus), from: 'jnk.pipeline@onlinepsbloans.com', mimeType: 'text/html', replyTo: '', subject: "Build Status ${currentBuild.currentResult}: Project name -> ${env.JOB_NAME}", to: Constants.ANS_EMAILTO + ",cc:" + Constants.ANS_EMAILTO_CC  // emailext used to send attachment, simple mail not able to send attachment.    
                            }
			    if(buildTeamsNotificationRequire){
				office365ConnectorSend webhookUrl: Constants.ANS_TEAMS_NOTIFICATION_WEBHOOK,
				message: MailBody('buildMailBody','','',SonarStatus),
				status: "Build ${currentBuild.currentResult}",
				remarks: ""
			    }
                        }
                    }
                }
            }
            stage("Archive Artifacts")
            {
                when {
                    expression {BuildSuccessfully}
                } 	
                steps{
                    //archiveArtifacts artifacts:'service-*/target/service-*.jar', fingerprint: true
			        archiveArtifacts artifacts:'dist/**/*.*', fingerprint: true
                    
                
                }
                post{
                    failure{
                        script {
                            if(Constants.ANS_BUILD_ARCHIVE_FAILURE_MAIL_REQUIRE){
                                mail bcc: '', body: '' + MailBody('archiveFailurMailBody'), cc: Constants.ANS_EMAILTO_CC, charset: 'UTF-8', from: 'jnk.pipeline@onlinepsbloans.com', mimeType: 'text/html', replyTo: '', subject: "(Web Project) Archiving Artifacts : Failed for  ${env.JOB_NAME} (Build Number : ${env.BUILD_NUMBER})", to: Constants.ANS_EMAILTO
                            }
                        }
                    }
                }

            }
            stage("Depolyment on Service")
            {
                steps{
                    sh "rm -rf /var/lib/jenkins/workspace/"+ base_service_name + "/src/main/resources/static/*"
                    sh "cp -RT /var/lib/jenkins/jobs/" + mainJobName + "/branches/${env.BRANCH_NAME}/builds/${env.BUILD_NUMBER}/archive/dist/* /var/lib/jenkins/workspace/" + base_service_name + "/src/main/resources/static/" 
                    
                }
                post{
                    always
                    {
                        script{
                            if (deploymentMailRequire){
                                mail bcc: '', body: '' + MailBody('webToServiceDeployment','','To Service'), cc:  Constants.ANS_EMAILTO_CC, charset: 'UTF-8', from: 'jnk.pipeline@onlinepsbloans.com', mimeType: 'text/html', replyTo: '', subject: "${currentBuild.currentResult} : Deployment  on " + serverName + " server is ${currentBuild.currentResult}" , to: Constants.ANS_EMAILTO
                            }
			    if(deploymentTeamsNotificationRequire){
				office365ConnectorSend webhookUrl: Constants.ANS_TEAMS_NOTIFICATION_WEBHOOK,
				message: MailBody('webToServiceDeployment','','To Service'),
				status: "Deployment ${currentBuild.currentResult}",
				remarks: ""
			    }
                        }
                        
                        
                    }
                }
            }
            stage ('Starting downstream job ') {
              steps {
		      build job: base_service_name, parameters: [string(name: 'BranchName', value: "${env.BRANCH_NAME}")]
              }
            }
            
        }
    }
}
