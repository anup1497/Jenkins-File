import org.ans.Constants

def call(String bodyType, String sonarURL='', deploymentServerName='',SonarStatus='') {
 
  def mailBody = ""
 def color =""
   
        switch(SonarStatus)
        {
          case Constants.SonarStatus.ByPassed : 
                
               color = "red"
               break;
          
           case Constants.SonarStatus.Passed :
              color = "green"
               break;
         
        }
        switch(bodyType){

        case "buildMailBody" :

                mailBody += "<html><table border='1' width='100%'>"
                mailBody += "<tr><td colspan='2'><b>Build Status :</b></td></tr>"
                mailBody += "<tr><td width='25%'><b>Project : </b></td><td width='75%'> ${env.JOB_NAME} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Build Number : </b></td><td width='75%'> ${env.BUILD_NUMBER} </td></tr>"
                mailBody += "<tr><td width='25%'><b>URL build : </b></td><td width='75%'> ${env.BUILD_URL} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Sonar Status : </b></td><td width='75%'><p style='color:" + color + "'><b>" + SonarStatus + "</p></td></tr>"
                mailBody += "<tr><td colspan='2'><b>Change Logs :</b></td></tr>"

                def MAX_MSG_LEN = 100
                //def changeString = "<b>Changes :</b> <br/>"
                def haveChages = "No"

                    def changeLogSets = currentBuild.changeSets
                    for (int i = 0; i < changeLogSets.size(); i++) {
                        def entries = changeLogSets[i].items
                        for (int j = 0; j < entries.length; j++) {
                            def entry = entries[j]
                            mailBody += "<tr><td colspan='2'><a href=${env.ANS_GIT_URL}/${env.JOB_NAME}/commit/${entry.commitId}> ${entry.commitId}</a> by <b>${entry.author}</b> on ${new Date(entry.timestamp)}</td></tr>"
                            mailBody += "<tr><td><b>Commit Message : </b></td><td> ${entry.msg} </td></tr>"
                            mailBody += "<tr><td><b> ACTION</b></td><td><b> File Name </b> </td></tr>"
                            def files = new ArrayList(entry.affectedFiles)
                            for (int k = 0; k < files.size(); k++) {
                                def file = files[k]
                                mailBody += "<tr><td><b>${file.editType.name} </td><td>${file.path} </td></tr>"
                            }
                            haveChages = "Yes"
                        }
                    }

                    if (haveChages=="No") {
                        mailBody += "<tr><td colspan='2'> - No new changes </td></tr>"
                    }
                    mailBody += "</table></html>"
                    break;
        
        case "deploymentMailBody" :

                mailBody += "<html><table border='1' width='100%' style='border: inset;'>"
                mailBody += "<tr><td colspan='2' style='background-color: cornflowerblue;color: white;'><b>Deployement Status :</b></td></tr>"
                mailBody += "<tr><td width='25%'><b>Project : </b></td><td width='75%'> ${env.JOB_NAME} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Build Number : </b></td><td width='75%'> ${env.BUILD_NUMBER} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Build URL : </b></td><td width='75%'> ${env.BUILD_URL} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Deployment Server : </b></td><td width='75%'>" +  deploymentServerName + "</td></tr>"
                mailBody += "</table></html>"
                break;

        case "archiveFailurMailBody" :
        
                mailBody += "<html><table border='1' width='100%' style='border: inset;'>"
                mailBody += "<tr><td colspan='2' style='background-color: cornflowerblue;color: white;'><b>Archiving Artifacts </b></td></tr>"
                mailBody += "<tr><td width='25%'><b>Project : </b></td><td width='75%'> ${env.JOB_NAME} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Build Number : </b></td><td width='75%'> ${env.BUILD_NUMBER} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Build URL : </b></td><td width='75%'> ${env.BUILD_URL} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Status : </b></td><td width='75%'> <b>Failed</b> </td></tr>"
                mailBody += "</table></html>"
                break;
         
         case "sonarFailureMailBody" :
        
                mailBody += "<html><table border='1' width='100%' style='border: inset;'>"
                mailBody += "<tr><td colspan='2' style='background-color: cornflowerblue;color: white;'><b>SONAR TEST FAIL </b></td></tr>"
                mailBody += "<tr><td width='25%'><b>Project : </b></td><td width='75%'> ${env.JOB_NAME} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Build Number : </b></td><td width='75%'> ${env.BUILD_NUMBER} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Build URL : </b></td><td width='75%'> ${env.BUILD_URL} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Status : </b></td><td width='75%'> <b>Failed</b> </td></tr>"
                mailBody += "<tr><td widht='25%'><b>Sonar URL : </b></td><td width='75%'><a href='" + sonarURL +"'>" + sonarURL + "</a></td></tr>"
                mailBody += "</table></html>"
                break;
         
         case "webToServiceDeployment" :
         
                mailBody += "<html><table border='1' width='100%' style='border: inset;'>"
                mailBody += "<tr><td colspan='2' style='background-color: cornflowerblue;color: white;'><b>Deployement Status :</b></td></tr>"
                mailBody += "<tr><td width='25%'><b>Project : </b></td><td width='75%'> ${env.JOB_NAME} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Build Number : </b></td><td width='75%'> ${env.BUILD_NUMBER} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Build URL : </b></td><td width='75%'> ${env.BUILD_URL} </td></tr>"
                mailBody += "<tr><td width='25%'><b>Deployment Server : </b></td><td width='75%'>" +  deploymentServerName + "</td></tr>"
                mailBody += "</table></html>"
                break;
         
        }
 
        return mailBody
    
}
