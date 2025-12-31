def call() {
  try {
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
    // Health check passed
    mail to: "duydeptrai2004tv@gmail.com",
         subject: "${JOB_NAME} - #${BUILD_NUMBER} - HEALTH CHECK PASS",
         body: "App is healthy.\n${BUILD_URL}"

  } catch (err) {
    // Health check failed (or deploy failed)
    mail to: "duydeptrai2004tv@gmail.com",
         subject: "${JOB_NAME} - #${BUILD_NUMBER} - HEALTH CHECK FAIL",
         body: "App is NOT healthy or deploy failed.\nCheck console:\n${BUILD_URL}"
    throw err
  }
}
