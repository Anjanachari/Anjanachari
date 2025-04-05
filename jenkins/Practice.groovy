pipeline{
    agent any
    stages{
        stage('Git checkout and clone'){
            steps{
                git url: 'https://github.com/Anjanachari/jenkins2.git'
            }
        }
        stage('Build'){
            sh 'mvn clean install'

        }
        stage('Unit Test Cases'){
            sh 'mvn test'
        }
        stage('Code Quality'){
            sh 'mvn sonar:sonar'
        }
        stage('Building application'){
            sh 'mvn package'
        }
        stage(''){
            sh ''
        }
    }
}