import util.AnsibleVars;
import util.Pipeline;

folder('Deployment')
folder('Deployment/CI')

job('Deployment/CI/Build') {
    deliveryPipelineConfiguration('CI Env', 'Build')
    wrappers {
        deliveryPipelineVersion('build #$BUILD_NUMBER', true)
    }
    scm {
        git {
            remote {
                url('https://github.com/noidi/hello-java.git')
            }
            branch('master')
            extensions {
                cleanAfterCheckout()
            }
        }
    }
    triggers {
        scm('*/15 * * * *')
    }
    steps {
        shell('echo $BUILD_NUMBER > src/main/resources/build.txt')
        shell('mvn package')
        shell('mvn verify')
    }
    publishers {
        archiveJunit('target/failsafe-reports/*.xml')
        archiveArtifacts {
            pattern('target/hello.jar')
            pattern('start')
            pattern('stop')
            onlyIfSuccessful()
        }
        downstream('Deployment/CI/Deploy', 'SUCCESS')
    }
}

job('Deployment/CI/Deploy') {
    deliveryPipelineConfiguration('CI Env', 'Deploy')
    wrappers {
        buildName('$PIPELINE_VERSION')
    }
    Pipeline.checkOut(delegate)
    steps {
        copyArtifacts('Deployment/CI/Build') {
            buildSelector() {
                upstreamBuild(true)
            }
            includePatterns('**/*')
            flatten()
        }
        shell("ansible-playbook -i '${AnsibleVars.INVENTORY_FILE}' -l ci deploy.yml")
    }
    publishers {
        buildPipelineTrigger('Deployment/QA/Deploy')
    }
}
