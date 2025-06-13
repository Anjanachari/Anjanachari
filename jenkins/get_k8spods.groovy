pipeline {
    agent any

    environment {
        // Update this to a directory where Jenkins has access
        KUBECONFIG = 'C:/Users/anjan/.kube/config'  // Adjust this path as needed
    }

    stages {

        stage('Get Pods') {
            steps {
                script {
                    // Run kubectl to get all pods in all namespaces and save to file
                    sh 'kubectl get pods -A > C:/Users/anjan/pods_output.txt'
                }
            }
        }
    }
}

