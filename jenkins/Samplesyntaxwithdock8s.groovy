pipeline {
    agent any
// 1. Checkout Code from a Version Control System
    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/your-repo.git', branch: 'main'
            }
        }
// 2. Build the Application

        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
// 3. Run Unit Tests
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
// 4. Code Quality Analysis
        stage('Code Quality') {
            steps {
                sh 'mvn sonar:sonar'
            }
        }
// 5. Package the Application
        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }
// 6. Build Docker Image
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t your-image-name .'
            }
        }
// 7. Push Docker Image to a Registry
        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                    sh 'docker login -u $DOCKER_USER -p $DOCKER_PASS'
                    sh 'docker push your-image-name'
                }
            }
        }
// 8. Deploy to a Server

        stage('Deploy') {
            steps {
                sh 'kubectl apply -f deployment.yaml'
            }
        }
// 9. Run Integration Tests
        stage('Integration Test') {
            steps {
                sh 'mvn verify'
            }
        }
// 10. Notify Team Members
        stage('Notify') {
            steps {
                mail to: 'team@example.com',
                     subject: "Build ${currentBuild.fullDisplayName} Completed",
                     body: "Build ${currentBuild.fullDisplayName} has finished. Check the results at ${env.BUILD_URL}."
            }
        }
// 11. Archive Artifacts
        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
            }
        }
// 12. Cleanup Workspace
        stage('Cleanup') {
            steps {
                cleanWs()
            }
        }
    }
        post {
        success {
            echo 'Build was successful!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}

=================================================================
Optimizing the Pipeline:
Parallel Execution: You could run certain steps (like testing and Sonar analysis) in parallel to save time.
Caching: Use caching mechanisms for dependencies in both Maven and Docker to avoid rebuilding or re-downloading them every time.
Docker Layer Caching: Ensure that your Dockerfiles are optimized for caching to avoid unnecessary rebuilding of layers.
Split the Pipeline: Consider splitting the pipeline into smaller jobs (e.g., one for testing, one for Docker build, one for deployment) and running them in parallel or sequentially, depending on the dependencies.
=================================================================


Dockerfile

# Use the official OpenJDK image for Java applications
FROM openjdk:17-jdk-alpine

# Add the JAR file built by Maven into the container
COPY target/your-application.jar /app/your-application.jar

# Expose the port your app will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/your-application.jar"]

===================================
deployment.yaml
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: java-application-deployment
  labels:
    app: java-app
spec:
  replicas: 3  # Increase replicas for load distribution
  selector:
    matchLabels:
      app: java-app
  template:
    metadata:
      labels:
        app: java-app
    spec:
      containers:
      - name: java-app
        image: your-docker-repo/your-application-image:latest  # Replace with your Docker image
        ports:
        - containerPort: 8080  # The port your Java app is running on inside the container
        volumeMounts:
        - name: app-data
          mountPath: /app/data  # Path inside the container where data will be stored (optional)
        env:
        - name: JAVA_OPTS
          value: "-Xmx256m -Xms128m"  # Optional: JVM memory options for the Java application
      volumes:
      - name: app-data
        persistentVolumeClaim:
          claimName: java-app-pvc  # PVC claim to use the persistent volume
---
apiVersion: v1
kind: Service
metadata:
  name: java-app-service
spec:
  type: LoadBalancer  # Expose the service externally
  selector:
    app: java-app
  ports:
  - protocol: TCP
    port: 80        # The port that the service will expose externally
    targetPort: 8080  # The port where the Java application is running inside the container
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: java-app-pv
spec:
  capacity:
    storage: 1Gi
  accessModes:
    - ReadWriteMany  # This allows multiple pods to access the volume
  hostPath:
    path: /path/to/your/data  # Path on the host machine (use a directory on your nodes)
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: java-app-pvc
spec:
  accessModes:
    - ReadWriteMany  # This should match the PersistentVolume
  resources:
    requests:
      storage: 1Gi  # Request the same amount of storage as defined in the PV
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: java-app-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: your-app.example.com  # Replace with your domain name
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: java-app-service
            port:
              number: 80

===========================================================================================================

pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'your-image-name'   // Docker image name
        DOCKER_REGISTRY = 'your-docker-registry' // Docker registry (e.g., Docker Hub)
    }

    stages {
        // Stage 1: Checkout Code
        stage('Checkout') {
            steps {
                // Pull the latest code from the Git repository
                git url: 'https://github.com/your-repo.git', branch: 'main'
            }
        }

        // Stage 2: Build the Application
        stage('Build') {
            steps {
                // Clean and build the project using Maven
                sh 'mvn clean install'
            }
        }

        // Stage 3: Run Unit Tests
        stage('Test') {
            steps {
                // Run unit tests to verify functionality
                sh 'mvn test'
            }
        }

        // Stage 4: Code Quality Analysis
        stage('Code Quality') {
            steps {
                // Run SonarQube scan to check code quality
                sh 'mvn sonar:sonar'
            }
        }

        // Stage 5: Package the Application
        stage('Package') {
            steps {
                // Package the application into a distributable artifact (JAR)
                sh 'mvn package'
            }
        }

        // Stage 6: Build Docker Image
        stage('Build Docker Image') {
            steps {
                script {
                    // Dynamically generate Docker image version using the commit hash
                    def version = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    def imageTag = "${DOCKER_IMAGE}:${version}"
                    
                    // Build the Docker image with the version tag
                    sh "docker build -t ${imageTag} ."
                }
            }
        }

        // Stage 7: Push Docker Image to Registry
        stage('Push Docker Image') {
            steps {
                // Login to Docker registry using stored credentials
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                    sh 'docker login -u $DOCKER_USER -p $DOCKER_PASS'
                    sh 'docker push ${DOCKER_IMAGE}:${version}' // Push the Docker image with version tag
                }
            }
        }

        // Stage 8: Deploy to Kubernetes
        stage('Deploy') {
            steps {
                // Deploy the application to Kubernetes (using a deployment.yaml file)
                sh 'kubectl apply -f deployment.yaml'
            }
        }

        // Stage 9: Run Integration Tests
        stage('Integration Test') {
            steps {
                // Run integration tests after the application is deployed
                sh 'mvn verify'
            }
        }

        // Stage 10: Notify Team Members
        stage('Notify') {
            steps {
                // Send an email to notify the team about the build status
                mail to: 'team@example.com',
                     subject: "Build ${currentBuild.fullDisplayName} Completed",
                     body: "Build ${currentBuild.fullDisplayName} has finished. Check the results at ${env.BUILD_URL}."
            }
        }

        // Stage 11: Archive Build Artifacts
        stage('Archive Artifacts') {
            steps {
                // Archive the built JAR file for later use or debugging
                archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
            }
        }

        // Stage 12: Cleanup Workspace
        stage('Cleanup') {
            steps {
                // Clean up the workspace to avoid unnecessary build artifacts and clutter
                cleanWs()
            }
        }
    }

    // Optional: Define post-build actions for success/failure
    post {
        success {
            echo 'Build was successful!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
