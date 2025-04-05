pipeline {
    agent any
    environment {
        PATH = "$PATH:/opt/apache-maven-3.6.3/bin"
    }
    stages {
        stage('Checkout') {
            steps {
                // Clone the GitHub repository
                git url: "https://github.com/Anjanachari/jenkins2.git", branch: 'main'
            }
        }
       /* 
        stage('Clean and Package') {
            steps {
                script {
                    // Run the Maven build (adjust if using another build tool)
                    sh 'mvn clean package'
                }
            }
        }*/
        stage('Build'){
            steps{
                sh 'mvn clean package'
            }
        }
    }
}