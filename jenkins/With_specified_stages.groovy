pipeline {
    agent any
    
    parameters {
        choice(name: 'BRANCH', choices: ['main', 'develop', 'feature'], description: 'Select the branch to build')
        booleanParam(name: 'RUN_BUILD', defaultValue: true, description: 'Run Build stage?')
        booleanParam(name: 'RUN_TEST', defaultValue: true, description: 'Run Test stage?')
        booleanParam(name: 'RUN_DEPLOY', defaultValue: true, description: 'Run Deploy stage?')
    }
    
    stages {
        stage('Build') {
            when {
                expression { params.RUN_BUILD }
            }
            steps {
                echo "Building branch ${params.BRANCH}"
                // Add your build steps here, e.g. checkout scm: [$class: 'GitSCM', branches: [[name: "*/${params.BRANCH}"]]]
            }
        }
        
        stage('Test') {
            when {
                expression { params.RUN_TEST }
            }
            steps {
                echo "Testing branch ${params.BRANCH}"
                // Add your test steps here
            }
        }
        
        stage('Deploy') {
            when {
                expression { params.RUN_DEPLOY }
            }
            steps {
                echo "Deploying branch ${params.BRANCH}"
                // Add your deploy steps here
            }
        }
    }
}
