pipeline
{
    parameters
    {
        booleanParam (  name: 'DEPLOY',  defaultValue: false, description: 'when true artifact is deployed' )
        booleanParam (  name: 'RELEASE',  defaultValue: false, description: 'when true  a release is built' )
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
                expression { params.RELEASE }
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
                	expression { params.DEPLOY }
                }
                not
                { 
                	expression { params.RELEASE }
                }
            }
            steps
            {
                sh 'mvn --errors --batch-mode --settings $MVN_SETTINGS clean verify sonar:sonar -Psonar'
            }
        }
        stage('deploy')
        {
            when
            {
                expression { params.DEPLOY }
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
                expression { params.RELEASE }
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
