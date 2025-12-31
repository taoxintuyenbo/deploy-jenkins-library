def call() {
  stage('Deploy artifact on VM'){
     if(env.BRANCH_NAME == 'releases'){
      retry(2){
        timeout(time: 3, unit: 'MINUTES'){
          withCredentials([usernamePassword(credentialsId: "${NEXUS_CRED}", usernameVariable: 'NEXUS_ACC', passwordVariable: 'NEXUS_PASS')]){
            sshagent(["${PRIVKEY_SERV_VM}"]){
              sh "ssh -o StrictHostKeyChecking=no root@${URL_SERV_VM} 'test -f /opt/${NEXUS_ARTIFACT_ID}.jar && cp /opt/${NEXUS_ARTIFACT_ID}.jar /opt/${NEXUS_ARTIFACT_ID}2.jar || true'"
              sh "ssh root@${URL_SERV_VM} curl -v -u ${NEXUS_ACC} -o /opt/${NEXUS_ARTIFACT_ID}.jar http://${NEXUS_URL}/repository/${NEXUS_RELEASES_REPO}/${NEXUS_GROUP}/${NEXUS_ARTIFACT_ID}/${VERSION}/${NEXUS_ARTIFACT_ID}-${VERSION}-${env.BRANCH_NAME}.jar"
            }
          }
        }
      }
     }
     if(env.BRANCH_NAME.startsWith('uat')){
       def GIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
       retry(2){
         timeout(time: 3, unit: 'MINUTES'){
           withCredentials([usernamePassword(credentialsId: "${NEXUS_CRED}", usernameVariable: 'NEXUS_ACC', passwordVariable: 'NEXUS_PASS')]){
             sshagent(["${PRIVKEY_SERV_VM}"]){
               sh "ssh -o StrictHostKeyChecking=no root@${URL_SERV_VM} 'test -f /opt/${NEXUS_ARTIFACT_ID}.jar && cp /opt/${NEXUS_ARTIFACT_ID}.jar /opt/${NEXUS_ARTIFACT_ID}2.jar || true'"
               sh "ssh root@${URL_SERV_VM} curl -v -u ${NEXUS_ACC} -o /opt/${NEXUS_ARTIFACT_ID}.jar http://${NEXUS_URL}/repository/${NEXUS_RELEASES_REPO}/${NEXUS_GROUP}/${NEXUS_ARTIFACT_ID}/${VERSION}/${NEXUS_ARTIFACT_ID}-${VERSION}-${env.BRANCH_NAME}-${GIT_HASH}.jar "
             }
           }
         }
       }
     }
       
     sshagent(["${PRIVKEY_SERV_VM}"]) {
       sh "ssh -o StrictHostKeyChecking=no root@${URL_SERV_VM} 'nohup java -jar /opt/${NEXUS_ARTIFACT_ID}.jar > /opt/${NEXUS_ARTIFACT_ID}.log 2>&1 &'"  
    }
  }
}
