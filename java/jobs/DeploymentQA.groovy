import util.AnsibleVars;
import util.Pipeline;

folder('Deployment')
folder('Deployment/QA')

job('Deployment/QA/Deploy') {
    deliveryPipelineConfiguration('QA Env', 'Deploy')
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
        shell("ansible-playbook -i '${AnsibleVars.INVENTORY_FILE}' -l qa deploy.yml")
    }
    publishers {
        buildPipelineTrigger('Deployment/Staging/Deploy')
    }
}
