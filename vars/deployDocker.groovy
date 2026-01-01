def call(){
  stage('Deploy Docker'){
    if(env.BRANCH_NAME == 'release'){
      withCredentials([usernamePassword(credentialsId: "${NEXUS_CRED}", usernameVariable: 'NEXUS_ACC', passwordVariable: 'NEXUS_PASS')]){
        sshagent(["${PRIVKEY_SERV_VM}"]){
          sh "ssh -o StrictHostKeyChecking=no root@${URL_SERV_VM} docker image prune -a -f"
          sh "ssh root@${URL_SERV_VM} 'docker inspect ${NEXUS_ARTIFACT_ID} >/dev/null 2>&1 && docker commit ${NEXUS_ARTIFACT_ID} ${NEXUS_URL_DOCKER}/docker-releases:stable || true'"
          sh "ssh root@${URL_SERV_VM} 'echo ${NEXUS_PASS} | docker login -u ${NEXUS_ACC} --password-stdin ${NEXUS_URL_DOCKER}'"
          sh "scp ./docker-compose.yaml root@${URL_SERV_VM}:/root/docker-compose.yaml"
          sh "ssh root@${URL_SERV_VM} 'NEXUS_ARTIFACT_ID=${NEXUS_ARTIFACT_ID} VERSION=${NEXUS_URL_DOCKER}/docker-releases:${VERSION}-${env.BRANCH_NAME} docker compose up -d --force-recreate'"
        }
      }
    }
    if(env.BRANCH_NAME.startsWith('uat')){
      def GIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
      withCredentials([usernamePassword(credentialsId: "${NEXUS_CRED}", usernameVariable: 'NEXUS_ACC', passwordVariable: 'NEXUS_PASS')]){
        sshagent(["${PRIVKEY_SERV_VM}"]){
          sh "ssh -o StrictHostKeyChecking=no root@${URL_SERV_VM} docker image prune -a -f"
          sh "ssh root@${URL_SERV_VM} 'docker inspect ${NEXUS_ARTIFACT_ID} >/dev/null 2>&1 && docker commit ${NEXUS_ARTIFACT_ID} ${NEXUS_URL_DOCKER}/docker-releases:stable || true'"
          sh "ssh root@${URL_SERV_VM} 'echo ${NEXUS_PASS} | docker login -u ${NEXUS_ACC} --password-stdin ${NEXUS_URL_DOCKER}'"
          sh "scp ./docker-compose.yaml root@${URL_SERV_VM}:/root/docker-compose.yaml"
          sh "ssh root@${URL_SERV_VM} 'NEXUS_ARTIFACT_ID=${NEXUS_ARTIFACT_ID} VERSION=${NEXUS_URL_DOCKER}/docker-releases:${VERSION}-${env.BRANCH_NAME}-${GIT_HASH} docker compose up -d --force-recreate'"
        }
      }
    }
  }
}
