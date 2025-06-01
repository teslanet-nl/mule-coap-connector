pipeline
{
    parameters
    {
        booleanParam (  name: 'DEPLOY_SNAPSHOT',  defaultValue: false, description: 'when true snapshot is deployed' )
        booleanParam (  name: 'BUILD_RELEASE',  defaultValue: false, description: 'when true  a release is built' )
    }
    environment
    {
        MVN_SETTINGS = credentials( 'secret-teslanet-maven-settings.xml' )
        GNUPGHOME = '/var/lib/jenkins_keys/.gnupg'
    }
    agent
    { 
        dockerfile
        {
            filename 'AgentDockerfile'
            args '--network sonar_network --volume jenkins_keys:/var/lib/jenkins_keys --volume jenkinsagent_m2repo:/home/jenkins/.m2/repository --add-host $REPO_ALIAS' }
    }
    options
    { 
        buildDiscarder( logRotator(numToKeepStr: '30' ))
    }
    stages
    {
        stage('prepare')
        {
            when
            {
                expression { params.BUILD_RELEASE }
            }
            steps
            {
                sh '''
                    mvn --errors --batch-mode --settings $MVN_SETTINGS release:clean
                '''
            }
        }
        stage('verify')
        {
            when
            {
                not
                { 
                	expression { params.DEPLOY_SNAPSHOT }
                }
                not
                { 
                	expression { params.BUILD_RELEASE }
                }
            }
            steps
            {
                sh 'mvn --errors --batch-mode --settings $MVN_SETTINGS clean verify sonar:sonar -Psonar'
            }
        }
        stage('verify and deploy')
        {
            when
            {
                expression { params.DEPLOY_SNAPSHOT }
            }
             steps
            {
                sh 'mvn --errors --batch-mode --settings $MVN_SETTINGS clean deploy sonar:sonar -Psonar'
            }
        }
        stage('release')
        {
            when
            {
                expression { params.BUILD_RELEASE }
            }
            steps
            {
                sh 'mvn --errors --batch-mode --settings $MVN_SETTINGS clean release:prepare release:perform'
            }
            post
            {
                failure
                {
                    echo 'An unexpected error occurred. Rollbacking...'
                    sh 'mvn --errors --batch-mode --settings $MVN_SETTINGS release:rollback'
                }
            }
        }
    }
    post
    {
        // Clean after build
        always
        {
            cleanWs( cleanWhenNotBuilt: false,
                     deleteDirs: true,
                     disableDeferredWipeout: true,
                     notFailBuild: true
                   )
        }
    }
}
