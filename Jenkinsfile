pipeline {
    agent any
    stages {

        stage('# Build') {
            steps {
                // Run Maven on a Unix agent.
                sh "chmod +x ./mvnw &&./mvnw -Dmaven.test.failure.ignore=true clean verify"
            }

            post {
                // If Maven was able to run the tests, even if some of the test
                // failed, record the test results and archive the jar file.
                success {
                    junit '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts 'target/*.jar'
                }
            }
        }

        stage('发布') {
            steps {
                echo "发布中"
            }

            post {
                success {
                    echo "发布成功"
                }
            }
        }
    }
}