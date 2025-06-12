pipeline {
    agent any

    parameters {
        choice(name: 'BRANCH', choices: ['main', 'SIT', 'UAT', 'Pre_Prod'], description: 'Select branch to build')
        booleanParam(name: 'RUN_CODE_PULL', defaultValue: true, description: 'Run Code Pull stage?')
        booleanParam(name: 'RUN_CLEAN_BUILD', defaultValue: true, description: 'Run Clean Build stage?')
        booleanParam(name: 'RUN_UNIT_TESTS', defaultValue: true, description: 'Run Unit tests stage?')
        // booleanParam(name: 'RUN_CODE_QUALITY', defaultValue: false, description: 'Run Code Quality stage?') // uncomment if needed
        booleanParam(name: 'RUN_CODE_PACKAGE', defaultValue: true, description: 'Run Code Package stage?')
        booleanParam(name: 'RUN_INTEGRATION_TESTS', defaultValue: true, description: 'Run Integration Tests stage?')
        booleanParam(name: 'RUN_ARCHIVING', defaultValue: true, description: 'Run Archiving stage?')
        booleanParam(name: 'RUN_CLEAN_WORKSPACE', defaultValue: true, description: 'Run Clean Workspace stage?')
    }

    stages {
        stage('Code Pull') {
            when {
                expression { params.RUN_CODE_PULL }
            }
            steps {
                git url: 'https://github.com/Anjanachari/jenkins2.git', branch: "${params.BRANCH}"
            }
        }

        stage('Clean Build') {
            when {
                expression { params.RUN_CLEAN_BUILD }
            }
            steps {
                bat 'mvn clean install'
            }
        }

        stage('Unit tests') {
            when {
                expression { params.RUN_UNIT_TESTS }
            }
            steps {
                bat 'mvn test'
            }
        }

        /*
        stage('Code Quality with Sonar') {
            when {
                expression { params.RUN_CODE_QUALITY }
            }
            steps {
                bat 'mvn sonar:sonar'
            }
        }
        */

        stage('Code Package') {
            when {
                expression { params.RUN_CODE_PACKAGE }
            }
            steps {
                bat 'mvn package'
            }
        }

        stage('Integration Tests') {
            when {
                expression { params.RUN_INTEGRATION_TESTS }
            }
            steps {
                bat 'mvn verify'
            }
        }

        stage('Archiving') {
            when {
                expression { params.RUN_ARCHIVING }
            }
            steps {
                archiveArtifacts artifacts: '**/target/*.war', allowEmptyArchive: true
            }
        }

        stage('Clean Workspace') {
            when {
                expression { params.RUN_CLEAN_WORKSPACE }
            }
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
