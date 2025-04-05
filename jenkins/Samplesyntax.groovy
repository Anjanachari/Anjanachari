pipeline {
    agent any
    stages {
        stage ('Code Pull') {
            steps {
                    git url : 'https://github.com/Anjanachari/jenkins2.git' , branch : 'main'
            }
        }
        stage ('Clean Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage ('Unit tests') {
            steps {
                sh 'mvn test'
            }
        }
        stage ('Code Quality with Sonar') {
            steps {
                sh 'mvn sonar:sonar'
            }
        }
        stage ('Code Package') {
            steps {
                sh 'mvn package'
            }
        }
        stage ('Docker Image Build') {
            steps {
                sh 'docker build -t first-image:1 .'
            }
        }
        stage ('Docker Push') {
            steps {
                withCredentials([credentialsId:'credential-ID',passwordVariable:'Docker_PASS',usernameVariable:'Docker_USER']) {
                    sh 'docker login -u $Docker_USER $Docker_PASS'
                    sh 'docker push first-image:1'
                }
            }
        }
        stage ('Kubernetes Deployment') {
            steps {
                sh 'kubectl apply -f sample.yaml'
            }
        }
        stage ('Integration Tests') {
            steps {
                sh 'mvn verify'
            }
        }
        stage('Notify') {
            steps {
                mail to: 'anjanachari19@gmail.com',
                subject: "Build status of ${currentBuild.fullDisplayName}",
                body: "Build status of ${currentBuild.result} and check the logs here: ${env.BUILD_URL}"
            }
        }
        stage ('Archiving') {
            steps {
                archiveArtifacts artifact :'**/target/*.jar', allowEmptyArchive: true
            }
        }
        stage ('Clean Workspace') {
            steps {
                cleanWs()
            }
        }
    }
    post {
        success {
            echo 'Build success'
        }
        failure {
            echo 'Build Failed'
            mail to: 'anjanachari19@gmail.com',
                 subject: "Build Failed: ${currentBuild.fullDisplayName}",
                 body: "The build has failed. Please check the logs: ${env.BUILD_URL}"
        }
        always {
            echo 'Build status success or failure'
        }   
    }
}