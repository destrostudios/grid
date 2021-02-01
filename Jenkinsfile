node {
    ansiColor('xterm') {
        try {
            stage('Checkout') {
                checkout scm
            }
            stage('Build') {
                sh 'mkdir workspace'
                sh 'mvn clean install'
            }
        } finally {
            cleanWs()
        }
    }
}