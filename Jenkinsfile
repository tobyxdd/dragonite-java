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
    stage('deploy') {
      steps {
        echo 'should deploy'
      }
    }
  }
  post {
    always {
      archiveArtifacts '**/build/distributions/*.zip'
      mail  to: 'w@vecsight.com,t@vecsight.com,p@vecsight.com',
        subject: "Pipeline '${env.JOB_NAME}' ${env.BUILD_DISPLAY_NAME} resulted ${currentBuild.result}",
        body: "Build URL: ${env.BUILD_URL}"
    }
  }
}
