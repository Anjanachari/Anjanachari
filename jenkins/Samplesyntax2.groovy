pipeline {
    agent any
/*
    environment {
    // Ensure Maven and Java are set up correctly for Windows
    MAVEN_HOME = 'C:\\apache-maven-3.9.5'  // Adjust the path to your local Maven installation
    JAVA_HOME = 'C:\\Program Files\\Java\\jdk-11'  // Adjust the path to your local Java installation
    GIT_HOME = 'C:\\Program Files\\Git\\bin\\git.exe'  // Adjust the path to your local Git installation (if needed)
}


    tools {
        // If Git and Maven are installed in Jenkins Global Tool Configuration
        maven 'MAVEN_HOME'  // Assuming you named the installation 'Maven3' in Global Tool Configuration
        git 'Default_git'   // Assuming 'Default' is the name you gave to your Git installation
    }*/

    stages {
        stage('Code Pull') {
            steps {
                
                // Pull the code from the specified repository and branch
                git url: 'https://github.com/Anjanachari/jenkins2.git', branch: 'main'
            }
        }

        stage('Clean Build') {
            steps {
                // Run the Maven build command (use the full path for Windows)
                bat 'mvn clean install'  // Adjust Maven path for your system
            }
        }
        stage ('Unit tests') {
            steps {
                bat 'mvn test'
                
            }
        }
        /*
        stage ('Code Quality with Sonar') {
            steps {
                bat 'mvn sonar:sonar'
            }
        }*/
        stage ('Code Package') {
            steps {
                bat 'mvn package'
            }
        }
        stage ('Integration Tests') {
            steps {
                bat 'mvn verify'
            }
        }
        stage ('Archiving') {
            steps {
                archiveArtifacts artifacts :'**/target/*.war', allowEmptyArchive: true
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
