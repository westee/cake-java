String buildNumber = env.BUILD_NUMBER;
String timestamp = new Date().format('yyyyMMddHHmmss');
String projectName = env.JOB_NAME.split(/\//)[0];

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

    sh('./mvnw clean package')

    deployVersion(version)
}

def deployVersion(String version) {
    println 'do rollback'
    echo version
    // sh "链接服务器使用docker发版"
}

def rollback() {
    println 'do rollback'
}