pipeline {
    agent any
    stages {
        stage('Test') {
            steps {
                sh './mvnw -Dmaven.test.failure.ignore=true clean package'
            }
        }
    }
}



