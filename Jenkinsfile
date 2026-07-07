pipeline {
    agent any
    environment {
        IMAGE_NAME     = "vshalgargnpl/ludo-engine"
        TAG            = "latest"
        CONTAINER_NAME = "ludo-engine"
        PORT_MAPPING   = "808X:808X"  // fix port as final
        REGISTRY       = "index.docker.io"
    }
    parameters {
        string(name: 'BRANCH_NAME',
         defaultValue: 'ludo-engine-dev',
          description: 'Branch to build')
    }
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out ludo-engine source code...'
                git branch: "${params.BRANCH_NAME}", url: 'git@github.com:vshalgarg/game-application.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Ludo Engine Docker image...'
                // here docker file using layer cache to make jar file
                sh 'docker build -t $IMAGE_NAME:$TAG .'
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    echo 'Pushing image to Docker Hub...'

                    docker.withRegistry("https://${REGISTRY}", 'docker-credentials-id') {
                        docker.image("$IMAGE_NAME:$TAG").push()
                    }
                }
            }
        }

        stage('Stop & Remove Existing Container') {
            steps {
                echo 'Cleaning up old container if running...'
                sh 'docker rm -f $CONTAINER_NAME || true'
            }
        }

        stage('Run Container') {
            steps {
                echo 'Starting Ludo Engine container...'

                sh '''
                docker run -d \
                  --name "$CONTAINER_NAME" \
                  -p "$PORT_MAPPING" \
                  --restart unless-stopped \
                  -e SPRING_PROFILES_ACTIVE=dev \
                  -v /var/log/ludo-engine:/logs \
                  "$IMAGE_NAME:$TAG"
                '''
            }
        }

        stage('Clean Old Images') {
            steps {
                echo 'Pruning unused docker images...'
                sh 'docker image prune -f'
            }
        }
    }
}