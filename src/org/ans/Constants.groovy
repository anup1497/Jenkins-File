package org.ans

class Constants {
  static final String ANS_EMAILTO = "pappu.yadav@onlinepsbloans.com,harshit.suhagiya@onlinepsbloans.com,tn-it@onlinepsbloans.com"
  //static final String ANS_EMAILTO = "jignesh.mirani@onlinepsbloans.com"
  static final String ANS_EMAILTO_CC = "jignesh.mirani@onlinepsbloans.com"
  
  static final String PROJECT_NAME = "TN"
  
  static final String ANS_TEAMS_NOTIFICATION_WEBHOOK = "https://capitaworld.webhook.office.com/webhookb2/87fa1920-e741-4ec6-b78e-adefe12dbe95@54318472-f71e-43cb-b853-5fffed1d2cad/IncomingWebhook/af30d89e7cbf47309ec43be6d36e089d/ea5433fa-1fa9-4ab0-af86-35344324e4b5"
   

  static final String ANS_SIT_BRANCH_SERVERNAME = "SIT"
  static final boolean ANS_SIT_BUILD_MAIL_REQUIRE = true
  static final boolean ANS_SIT_DEPLOY_MAIL_REQUIRE = true
  static final boolean ANS_SIT_BUILD_TEAMS_NOTIFICATION_REQUIRE = true
  static final boolean ANS_SIT_DEPLOY_TEAMS_NOTIFICATION_REQUIRE = true
  static final String ANS_SIT_DEPLOY_FILENAME = "deployall.sh"
  static final String ANS_SIT_SERVER_JENKINPATH = "/apps/services/jenkinsPath"
  
  
  static final String ANS_QA_BRANCH_SERVERNAME = "QA"
  static final boolean ANS_QA_BUILD_MAIL_REQUIRE = true
  static final boolean ANS_QA_DEPLOY_MAIL_REQUIRE = true
  static final boolean ANS_QA_BUILD_TEAMS_NOTIFICATION_REQUIRE = true
  static final boolean ANS_QA_DEPLOY_TEAMS_NOTIFICATION_REQUIRE = true
  static final String ANS_QA_DEPLOY_FILENAME = "deployall.sh"
  static final String ANS_QA_SERVER_JENKINPATH = "/apps/services/jenkinsPath"
  
  
  static final String ANS_RELEASE_BRANCH_SERVERNAME = "UAT"
  static final boolean ANS_UAT_BUILD_MAIL_REQUIRE = true
  static final boolean ANS_UAT_DEPLOY_MAIL_REQUIRE = true
  static final boolean ANS_UAT_BUILD_TEAMS_NOTIFICATION_REQUIRE = true
  static final boolean ANS_UAT_DEPLOY_TEAMS_NOTIFICATION_REQUIRE = true
  static final String ANS_UAT_DEPLOY_FILENAME = "deployall.sh"
  static final String ANS_UAT_SERVER_JENKINPATH = "/apps/services/jenkinsPath"
  
  static final String ANS_MASTER_BRANCH_SERVERNAME = "PRODUCTION"
  static final boolean ANS_PROD_BUILD_MAIL_REQUIRE = false
  static final boolean ANS_PROD_DEPLOY_MAIL_REQUIRE = false
  static final boolean ANS_PROD_BUILD_TEAMS_NOTIFICATION_REQUIRE = false
  static final boolean ANS_PROD_DEPLOY_TEAMS_NOTIFICATION_REQUIRE = false
  static final String ANS_PROD_DEPLOY_FILENAME = "deployall.sh"
  static final String ANS_PROD_SERVER_JENKINPATH = "/apps/services/jenkinsPath"
  
  static final boolean ANS_SONAR_FAILURE_MAIL_REQUIRE = false
  static final boolean ANS_SONAR_TEST_PERFORM = false
  static final String ANS_SONAR_PERFORM_BRANCH = "release"
  static final boolean ANS_BUILD_ARCHIVE_FAILURE_MAIL_REQUIRE = false
  
  
  
  
  enum SonarStatus{
    ByPassed,
    Passed
  } 
  
}
