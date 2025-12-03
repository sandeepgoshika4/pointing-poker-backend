pipeline {
    agent any

    environment {
        DOCKERHUB_USER = "sandeepgoshika4"
        IMAGE_NAME = "poker-backend"
        KUBECONFIG = credentials('.kube/config')
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                sh """
                    echo "Building Spring Boot Application..."
                    mvn clean package -DskipTests
                """
            }
        }

        stage('Build & Push Docker Image (ARM64)') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                 usernameVariable: 'USER',
                                                 passwordVariable: 'PASS')]) {

                    sh """
                        echo "Logging into Docker Hub..."
                        echo "$PASS" | docker login -u "$USER" --password-stdin

                        echo "Building ARM64 Docker image..."
                        docker buildx build \\
                            --platform linux/arm64 \\
                            --file Dockerfile \\
                            -t $DOCKERHUB_USER/$IMAGE_NAME:latest \\
                            . \\
                            --push
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh """
                    echo "Deploying to Raspberry Pi Kubernetes cluster..."
                    export KUBECONFIG=$KUBECONFIG

                    kubectl apply -n poker-app -f /home/jenkins/k8s/poker-app/backend-deployment.yaml
                    kubectl apply -n poker-app -f /home/jenkins/k8s/poker-app/backend-service.yaml
                """
            }
        }
    }
}
