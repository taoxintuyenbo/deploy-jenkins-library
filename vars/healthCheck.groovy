def call() {
  stage('Health Check') {
    sleep 30
    retry(3) {
      def r = httpRequest(
        url: "http://${URL_SERV_VM}:8080",
        validResponseCodes: '200:399',
        timeout: 10
      )
      echo "Health status: ${r.status}"
      sleep 5
    }
  }
} 
