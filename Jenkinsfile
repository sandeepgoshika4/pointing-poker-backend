pipeline {
    agent any

    environment {
        DOCKERHUB_USER = "sandeepgoshika4"
        IMAGE_NAME     = "poker-backend"

        // Versioning
        BUILD_VERSION  = "v${BUILD_NUMBER}"
        GIT_COMMIT_SHORT = "${env.GIT_COMMIT[0..6]}"

        KUBECONFIG = credentials('kubeconfig-pi')
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

                        echo "Building ARM64 Docker image with tags:"
                        echo " - $BUILD_VERSION"
                        echo " - $BUILD_VERSION-${GIT_COMMIT_SHORT}"
                        echo " - latest"

                        docker buildx build \
                            --platform linux/arm64 \
                            --file Dockerfile \
                            -t $DOCKERHUB_USER/$IMAGE_NAME:$BUILD_VERSION \
                            -t $DOCKERHUB_USER/$IMAGE_NAME:$BUILD_VERSION-${GIT_COMMIT_SHORT} \
                            -t $DOCKERHUB_USER/$IMAGE_NAME:latest \
                            . \
                            --push
                    """
                }
            }
        }

        stage('Patch Deployment to Use New Version') {
            steps {
                sh """
                    echo "Updating backend deployment image to ${BUILD_VERSION}..."

                    export KUBECONFIG=$KUBECONFIG

                    kubectl -n poker-app set image deployment/poker-backend \
                      poker-backend=$DOCKERHUB_USER/$IMAGE_NAME:$BUILD_VERSION
                """
            }
        }

        stage('Apply Kubernetes YAML') {
            steps {
                sh """
                    echo "Applying backend deployment + service configs..."
                    export KUBECONFIG=$KUBECONFIG

                    kubectl apply -n poker-app -f /home/jenkins/k8s/poker-app/backend-service.yaml
                """
            }
        }
    }
}
