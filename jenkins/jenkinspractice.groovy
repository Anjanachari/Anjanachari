pipeline {
    agent any
    
    environment {
        DOCKER_CREDENTIALS_ID = '5d339e10-6bf8-4908-a48d-e9d02db872c7'
        DOCKER_IMAGE_NAME = 'anjanachari/second_image'
        DOCKER_REGISTRY = 'https://hub.docker.com' // Docker Hub registry URL
        GIT_REPO_URL = 'https://github.com/Anjanachari/jenkins2.git'
    }
    
    stages {
        stage('Checkout') {
            steps {
                // Clone the GitHub repository
                git url: "${env.GIT_REPO_URL}", branch: 'main'
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
        stage('Build Docker Image') {
            steps {
                script {
                    // Build the Docker image using shell command
                    sh "docker build -t ${env.DOCKER_IMAGE_NAME} ."
                }
            }
        }
        
        stage('Push Docker Image') {
            steps {
                script {
                    // Log in to Docker Hub
                    withCredentials([usernamePassword(credentialsId: "${env.DOCKER_CREDENTIALS_ID}", passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh "echo ${DOCKER_PASSWORD} | docker login ${env.DOCKER_REGISTRY} -u ${DOCKER_USERNAME} --password-stdin"
                        
                        // Tag the Docker image
                        sh "docker tag ${env.DOCKER_IMAGE_NAME} ${DOCKER_USERNAME}/${env.DOCKER_IMAGE_NAME}:latest"
                        
                        // Push the Docker image to Docker Hub
                        sh "docker push ${DOCKER_USERNAME}/${env.DOCKER_IMAGE_NAME}:latest"
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
