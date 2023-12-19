import groovy.transform.Field

String buildNumber = env.BUILD_NUMBER;
String timestamp = new Date().format('yyyyMMddHHmmss');
String projectName = env.JOB_NAME.split(/\//)[0];

userInputRegistryInfo = input(
    id: 'userInputRegistryInfo', message: 'Enter path of test reports:?',
    parameters: [
        string(description: 'Host of docker registry',
        name: 'host'),
        string(description: 'dockerImage of docker registry',
        name: 'dockerImage')
    ])

host = userInputRegistryInfo.host
dockerImage = userInputRegistryInfo.dockerImage

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
            pipelineTriggers([[$class: "SCMTrigger", scmpoll_spec: "* * * * *"]]),
            disableConcurrentBuilds()
    ];
    properties(propertiesArray);
}
@Field def inputAuthValue
def normalCIBuild(String version) {
    //stage ('test & package') {
    //    sh('chmod +x ./mvnw && ./mvnw clean package')
    //}

    stage('docker build') {
        inputAuthValue = getInputAuth()
        echo inputAuthValue.password

        sh("docker login ${host} -u ${inputAuthValue.username} -p ${inputAuthValue.password}")

        sh("docker build . -t ${host}/${dockerImage}:${version}")

        sh("docker push ${host}/${dockerImage}:${version}")

        stage('deploy')

        input 'deploy?'

        deployVersion(version)
    }
}

def deployVersion(String version) {
    // 链接服务器使用docker发版
    sh "ssh -o StrictHostKeyChecking=no root@${host.split(':')[0]} 'docker login ${host} -u ${inputAuthValue.username} -p ${inputAuthValue.password} && docker rm -f container-name && docker run --name container-name -d -p 8081:8080 ${host}/${dockerImage}:${version}'"
}

def rollback() {
    println 'do rollback'
    def getAllTagsUri = "/v2/${dockerImage}/tags/list";

    inputAuthValue = getInputAuth()
    sh("docker login ${host} -u ${inputAuthValue.username} -p ${inputAuthValue.password}")
    println "http://${host}${getAllTagsUri}"
    //def responseJson = new URL("http://${host}${getAllTagsUri}")
    //   .getText(requestProperties: ['Content-Type': "application/json"]);

    def responseJson = sh(script: "curl -u ${inputAuthValue.username}:${inputAuthValue.password} --url ${host}${getAllTagsUri}", returnOutput: true)

    println("responseJson")
    println(responseJson)

    // {name:xxx,tags:[tag1,tag2,...]}
    //Map response = new groovy.json.JsonSlurperClassic().parseText(responseJson) as Map;
    def xxmap = readJSON text: responseJson
    echo "xxmap["name"]"
    echo xxmap["name"]
    def versionsStr = xxmap.tags.join('\n');

    def rollbackVersion = input(
        message: 'Select a version to rollback',
        ok: 'OK',
        parameters: [choice(choices: versionsStr, description: 'version', name: 'version')])

    println rollbackVersion
    deployVersion(rollbackVersion)
}

def getInputAuth() {
    def inputAuth = input(
       id: 'getInputAuth', message: 'Enter username and password:?',
       parameters: [
          string(description: 'username of auth',
          name: 'username', trim: true),
          string(description: 'password of auth',
          name: 'password', trim: true )
       ])
    return inputAuth
}

