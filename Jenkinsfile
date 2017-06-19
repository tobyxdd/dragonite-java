
pipeline {
  agent {
    docker {
      image 'gradle'
    }
  }
  stages {
    stage('build') {
      steps {
        sh '''
gradle clean
gradle distZip
'''
      }
    }
    stage('test') {
      steps {
        echo 'should run tests'
      }
    }
    stage('archive') {
      steps {
        archiveArtifacts '**/build/distributions/*.zip'
        emailext to: 'w@vecsight.com,t@vecsight.com,p@vecsight.com',
          body: 'Artifacts:',
          subject: "Artifacts from pipeline '${env.JOB_NAME}' ${env.BUILD_DISPLAY_NAME}",
          attachmentsPattern: '**/build/distributions/*.zip'
      }
    }
    stage('deploy') {
      steps {
        script {
          try {
            timeout(time: 1, unit: 'HOURS') {
              mail to: 'w@vecsight.com,t@vecsight.com',
                mimeType: 'text/html',
                subject: "Pipeline '${env.JOB_NAME}' ${env.BUILD_DISPLAY_NAME} requests deployment confirm",
                body: "<a href=\"${env.BUILD_URL}input\">Click here to proceed or abort</a><br><br>Or ${env.BUILD_URL}input"
              input message: 'Deploy?'
              echo 'deploying to yoshino'
              sshagent(['ssh_yoshino']) {
                sh 'scp -o StrictHostKeyChecking=no ./dragonite-forwarder/build/distributions/dragonite-forwarder*.zip tobyxdd@yoshino.vecsight.com:/home/tobyxdd/jenkins/dragonite-forwarder.zip'
                sh 'ssh -o StrictHostKeyChecking=no tobyxdd@yoshino.vecsight.com "cd /home/tobyxdd/jenkins/; bash dragonited.sh"'
              }
              echo 'deploying to batman'
              sshagent(['ssh_batman']) {
                sh 'scp -o StrictHostKeyChecking=no ./dragonite-forwarder/build/distributions/dragonite-forwarder*.zip tobyxdd@batman.vecsight.com:/home/tobyxdd/jenkins/dragonite-forwarder.zip'
                sh 'ssh -o StrictHostKeyChecking=no tobyxdd@batman.vecsight.com "cd /home/tobyxdd/jenkins/; bash dragonited.sh"'
              }
            }
          } catch (err) {
            echo 'deployment aborted'
          }
        }
      }
    }
  }
  post {
    always {
      emailext to: 'w@vecsight.com,t@vecsight.com,p@vecsight.com',
        subject: "Pipeline '${env.JOB_NAME}' ${env.BUILD_DISPLAY_NAME} resulted ${currentBuild.currentResult}",
        body: "<a href=\"${env.BUILD_URL}\">Click here for more detail</a><br><br>Or ${env.BUILD_URL}",
        attachLog: true
    }
  }
}
