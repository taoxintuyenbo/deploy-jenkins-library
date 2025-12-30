def call () {
     
}
def call() {
  stage('Deploy artifact on VM'){
    if(env.BRANCH_NAME == 'releases'){
      retry(2){
        timeout(time: 3, unit: 'MINUTES'){
          withCredentials([usernamePassword(credentialsId: "${NEXUS_CRED}", usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]){
            sshagent(["${PRIVKEY_SERV_VM}"]){
              sh "ssh -o StrictHostKeyChecking=no root@${URL_SERV_VM} cp /opt/Board-game.jar /opt/Board-game2.jar"
              sh "ssh root@${URL_SERV_VM} curl -v -u ${NEXUS_ACC} -o /opt/Board-game.jar http://${NEXUS_URL}/repository/${NEXUS_RELEASES_REPO}/${NEXUS_GROUP}/${NEXUS_ARTIFACT_ID}/${VERSION}/${NEXUS_ARTIFACT_ID}-${VERSION}-${env.BRANCH_NAME}"
            }
          }
        }
      }
    }
    
   if(env.BRANCH_NAME.startsWith('uat')){
     def GIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
       retry(2){
         timeout(time: 3, unit: 'MINUTES'){
           withCredentials([usernamePassword(credentialsId: "${NEXUS_CRED}", usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]){
             sshagent(["${PRIVKEY_SERV_VM}"]){
               sh "ssh -o s"
             }
           }
         }
       }
   }
    
  }
}
