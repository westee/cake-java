String buildNumber = env.BUILD_NUMBER;
String timestamp = new Date().format('yyyyMMddHHmmss');
String projectName = env.JOB_NAME.split(/\//)[0];

def userInputRegistryInfo = input(
    id: 'userInputRegistryInfo', message: 'Enter path of test reports:?',
    parameters: [
        string(description: 'Host of docker registry',
        name: 'host'),
        string(description: 'dockerImage of docker registry',
        name: 'dockerImage')
    ])

String version = "${buildNumber}-${timestamp}-${projectName}";

node {
    checkout scm;

    if(params.BuildType=='Rollback') {
        return rollback()
    } else if(params.BuildType=='Normal'){
        return normalCIBuild(version)
    } else {
        setScmPollStrategyAndBuildTypes(['Normal', 'Rollback']);
    }
}

def setScmPollStrategyAndBuildTypes(List buildTypes) {
    def propertiesArray = [
            parameters([choice(choices: buildTypes.join('\n'), description: '', name: 'BuildType')]),
            pipelineTriggers([[$class: "SCMTrigger", scmpoll_spec: "* * * * *"]])
    ];
    properties(propertiesArray);
}

def normalCIBuild(String version) {
    stage 'test & package'

    sh('chmod +x ./mvnw && ./mvnw clean package')

    stage('docker build')

    def inputAuthValue = getInputAuth()

    sh("docker login ${userInputRegistryInfo.host} -u ${inputAuthValue.username} -p {inputAuthValue.password}")

    sh("docker build . -t ${userInputRegistryInfo.host}/${userInputRegistryInfo.dockerImage}:${version}")

    sh("docker push ${userInputRegistryInfo.host}/${userInputRegistryInfo.dockerImage}:${version}")

    stage('deploy')

    input 'deploy?'

    deployVersion(version)
}

def deployVersion(String version) {
    println 'do rollback'
    echo version
    // 链接服务器使用docker发版
    sh "ssh root@${userInputRegistryInfo.host.split(':')[0]} 'docker rm -f xxxx && docker run --name xxxx -d -p 8888:8080 ${userInputRegistryInfo.host}/${userInputRegistryInfo.dockerImage}:${version}'"
}

def rollback() {
    println 'do rollback'
    def dockerRegistryHost = userInputRegistryInfo.host;
    def getAllTagsUri = "/v2/${userInputRegistryInfo.dockerImage}/tags/list";

    def responseJson = new URL("${dockerRegistryHost}${getAllTagsUri}")
       .getText(requestProperties: ['Content-Type': "application/json"]);

    println(responseJson)

    // {name:xxx,tags:[tag1,tag2,...]}
    Map response = new groovy.json.JsonSlurperClassic().parseText(responseJson) as Map;

    def versionsStr = response.tags.join('\n');

    def rollbackVersion = input(
        message: 'Select a version to rollback',
        ok: 'OK',
        parameters: [choice(choices: versionsStr, description: 'version', name: 'version')])

    println rollbackVersion
    deployVersion(rollbackVersion)
}

def getInputAuth() {
    def getInputAuth = input(
       id: 'getInputAuth', message: 'Enter path of test reports:?',
       parameters: [
          string(description: 'username of auth',
          name: 'username', trim: true),
          string(description: 'password of auth',
          name: 'password ', trim: true )
       ])
    return getInputAuth
}

