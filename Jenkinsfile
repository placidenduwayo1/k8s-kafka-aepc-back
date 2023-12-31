pipeline {
    agent any
    tools {
        maven 'Maven-3.6.3'
        jdk 'OpenJdk-17'
    }
    stages {
        stage ('Build application services'){
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/placidenduwayo1/k8s-kafka-aepc-back.git']])
                dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-address/'){
                    sh 'mvn clean install'
                }
                dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-company/'){
                    sh 'mvn clean install'
                }
                dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-employee/'){
                    sh 'mvn clean install'
                }
                dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-project/'){
                    sh 'mvn clean install'
                }
                dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-gateway-service/'){
                    sh 'mvn clean install'
                }
                dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-ms-config-service/'){
                    sh 'mvn clean install'
                }
                dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-ms-registry-service/'){
                    sh 'mvn clean install'
                }
                
            }
            post {
                success {
                    dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-address/'){
                        archiveArtifacts '**/target/*.jar'
                    }
                    dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-company/'){
                        archiveArtifacts '**/target/*.jar'
                    }
                    dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-employee/'){
                        archiveArtifacts '**/target/*.jar'
                    }
                    dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-project/'){
                        archiveArtifacts '**/target/*.jar'
                    }
                    dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-gateway-service/'){
                        archiveArtifacts '**/target/*.jar'
                    }
                    dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-ms-config-service/'){
                        archiveArtifacts '**/target/*.jar'
                    }
                    dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-ms-registry-service/'){
                        archiveArtifacts '**/target/*.jar'
                    }
                }
            }
        }
        stage('Test business microservices') {
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/placidenduwayo1/k8s-kafka-aepc-back.git']])
                dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-address/'){
                    sh 'mvn test'
                }
                dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-company/'){
                    sh 'mvn test'
                }
                dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-employee/'){
                    sh 'mvn test'
                }
                 dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-project/'){
                    sh 'mvn test'
                }
            }
            post {
                always {
                    dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-address/'){
                        junit '**/target/surefire-reports/TEST-*.xml'
                    }
                    dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-company/'){
                        junit '**/target/surefire-reports/TEST-*.xml'
                    }
                    dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-employee/'){
                        junit '**/target/surefire-reports/TEST-*.xml'
                    }
                    dir('K8s-Kafka-AEPC-Back/k8s-kafka-aepc-clean-archi-bs-ms-project/'){
                        junit '**/target/surefire-reports/TEST-*.xml'
                    }
                }
            }
        }
        stage ('Build docker images') {
            steps {
                echo 'Starting to build docker image'
                script {
                    sh 'docker compose -f kafka-ms-docker-compose.yml down'
                    sh 'docker compose -f kafka-ms-docker-compose.yml build'
                    sh 'docker system prune -f'
                }
            }
        }
        stage ('Publish docker images') {
            steps {
                echo 'Starting to publish docker images into docker registry'
                script {
                    withDockerRegistry([ credentialsId: 'dockerhub-credentials', url: '' ]) {
                        sh 'docker compose -f kafka-ms-docker-compose.yml push'
                    }
                }
            }
        }
        stage ('Run the stack'){
            steps {
                echo 'Start running microservices containers of the application'
                script {
                    sh 'docker compose -f kafka-ms-docker-compose.yml up -d'
                }
            }
        }
    }
}