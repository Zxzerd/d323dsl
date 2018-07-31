job('Mikhail Znak/MNTLAB-mznak-main-build-job'){
   scm {
        github('MNT-Lab/d323dsl', '$BRANCH_NAME')
    }
  
  configure {
    project->
        project / 'properties' << 'hudson.model.ParametersDefinitionProperty' {
        parameterDefinitions {
            'com.cwctravel.hudson.plugins.extended__choice__parameter.ExtendedChoiceParameterDefinition' {
                name 'JOBS'
                quoteValue 'false'
                saveJSONParameterToFile 'false'
                visibleItemCount '5'
                type 'PT_CHECKBOX'
                value '''MNTLAB-mznak-child1-build-job,
                        MNTLAB-mznak-child2-build-job,
                        MNTLAB-mznak-child3-build-job,
                        MNTLAB-mznak-child4-build-job'''
                multiSelectDelimiter ','
                projectName "MNTLAB-mznak-main-build-job"
            }
        }
     }
  }
  
  parameters{
      gitParameterDefinition{
        name('BRANCH_NAME')
        type('BRANCH')
        defaultValue('origin/mznak')
        description('')
        branch('') 
        branchFilter('origin/(master|mznak)')
        tagFilter('.*')
        sortMode('NONE')
        selectedValue('TOP')
        useRepository('')
        quickFilterEnabled(false)
        listSize('1')
      }
  }
  steps {
        downstreamParameterized {
            trigger('$JOBS') {
                block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                }
                parameters {
                    predefinedProp('BRANCH_NAME', '$BRANCH_NAME')
                }
        }
    }
  }
}


for(i in 1..4){
  job('Mikhail Znak/MNTLAB-mznak-child'+i+'-build-job'){
    
    parameters{
      gitParam('BRANCH_NAME'){
        type('BRANCH')
      }
    }
	steps {
        shell('''/bin/bash $WORKSPACE/script.sh > $WORKSPACE/output.txt;
				 tar -czvf "$BRANCH_NAME"_dsl_script.tar.gz -C $WORKSPACE dsl.groovy''')
    }     
    scm {
        github('MNT-Lab/d323dsl', '$BRANCH_NAME')
    } 
    publishers {
      archiveArtifacts('output.txt, ${BRANCH_NAME}_dsl_script.tar.gz')
    }
  }
}