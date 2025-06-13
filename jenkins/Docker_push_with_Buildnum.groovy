pipeline {
    agent any
    
    environment {
        DOCKER_CREDENTIALS_ID = '57f23e61-e22d-4ade-a077-d7e7e87997cf'
        DOCKER_IMAGE_NAME = 'second_image'  // image name without username prefix
        DOCKER_REGISTRY = ''  // Docker Hub doesn't need registry URL here
        GIT_REPO_URL = 'https://github.com/Anjanachari/jenkins2.git'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git url: "${env.GIT_REPO_URL}", branch: 'main'
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${env.DOCKER_IMAGE_NAME} ."
            }
        }
        
        stage('Approval for Pushing Docker Image') {
            steps {
                input message: 'Do you want to push the Docker image to the registry?', ok: 'Yes, Push'
            }
        }
        
        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${env.DOCKER_CREDENTIALS_ID}", passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    script {
                        def versionTag = "${env.BUILD_NUMBER}" // Using Jenkins build number as version
                        echo "Docker image tag: ${versionTag}"
                        
                        sh """
                            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                            docker tag ${DOCKER_IMAGE_NAME} ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${versionTag}
                            docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE_NAME}:${versionTag}
                        """
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
