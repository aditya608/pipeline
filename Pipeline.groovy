#!groovy

def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

    pipeline {

        agent any
 
    //    parameters {  
		
    //             choice(name: "COMPONENT", 
    //             choices: "Wave\nWaveng\nSRPlus\nDACH\nDACH_API", 
    //             description: "Select component for config Tranfermation" )

    //             choice(name: "TransferEnvironments", 
    //             choices: "Alpha to Beta\nBeta to Prod", 
    //             description: "Select to download and publish to respective environment" )

    //             string( defaultValue: '',
    //             description: 'Specify the Package ZIP Version',
    //             name: 'ZIPVersion' )

    //             string( defaultValue: '',
    //             description: 'Pass the Eikon APP to Publish',
    //             name: 'APPNAME' )

    //             string( defaultValue: '',
    //             description: 'Specify the Target EikonVersion',
    //             name: 'EIKONVERSION' )

	// }
	

    environment  { 
	
      
       name = "${pipelineParams.name}"

	}

         options {
            skipDefaultCheckout()
            gitLabConnection('GitLab_Generic')
            timeout(time: 60, unit: 'MINUTES')
        }

        triggers {
        gitlab(triggerOnPush: true, triggerOnMergeRequest: true)
        }

        stages {
            stage('Checkout Branch') {
                steps {
                   cleanWs()
                    checkout scm
                }
            }

            // stage('Pre-build-1'){
            //     parallel{
            //         stage('Alpha to Beta'){
            //             when{
            //                 environment name: 'TransEnvs', value: 'Alpha to Beta'
            //             }
            //             steps{
            //                 script{
            //                     env.SourceEnvironment="Alpha"
            //                     env.TargetEnvironment="Beta"                               
            //                 }
            //             }
            //         }

            //         stage('Beta to Prod'){
            //             when{
            //                 environment name: 'TransEnvs', value: 'Beta to Prod'
            //             }
            //             steps{
            //                 script{
            //                     env.SourceEnvironment="Beta"
            //                     env.TargetEnvironment="Prod"                               
            //                 }
            //             }
                    
            //         }

                    
            //     }
            // }

            stage('diplay'){
                steps{
                    script{
                        echo "${env:name}"

                    }
                }
            }
            stage('Pre-build-3'){
                steps {
                    script {
                    properties([
                            parameters([
                                [$class: 'ChoiceParameter', 
                                    choiceType: 'PT_SINGLE_SELECT', 
                                    description: 'Select the Component from the Dropdown List', 
                                    filterLength: 1, 
                                    filterable: false, 
                                    name: 'Component', 
                                    script: [
                                        $class: 'GroovyScript', 
                                        fallbackScript: [
                                            classpath: [], 
                                            sandbox: false, 
                                            script: 
                                                "return['Could not get The component']"
                                        ], 
                                        script: [
                                            classpath: [], 
                                            sandbox: false, 
                                            script: 
                                                "return['Wave','Wave-ng','SRPlus','DACH','DACH_API']"
                                        ]
                                    ]
                                ],

                                [$class: 'CascadeChoiceParameter', 
                                    choiceType: 'PT_CHECKBOX', 
                                    description: 'Select the sub-component for WAVE',
                                    name: 'sub-component', 
                                    referencedParameters: 'Component', 
                                    script: 
                                        [$class: 'GroovyScript', 
                                        fallbackScript: [
                                                classpath: [], 
                                                sandbox: false, 
                                                script: "return['Could not get component from Component Param']"
                                                ], 
                                        script: [
                                                classpath: [], 
                                                sandbox: false, 
                                                script: '''
                                                
                                                if (Component.equals("Wave")){
                                                    return["HVMI_API", "HVMI_API_SRV", "HVMI_SRV", "HVMI_EXTERNAL_API"]
                                                }
 
                                             
                                               '''
                                            ] 
                                    ]
                                ],
                                
                                choice( name: "TransferEnvironments", 
                                        choices: ["Alpha to Beta","Beta to Prod"],
                                        description: "Select to download and publish to respective environment" ),

                                string( defaultValue: '',
                                        description: 'Specify the Package ZIP Version',
                                        name: 'ZIPVersion' ),

                                string( defaultValue: '',
                                        description: 'Pass the Eikon APP to Publish',
                                        name: 'APPNAME' ),

                                string( defaultValue: '',
                                        description: 'Specify the Target EikonVersion',
                                        name: 'EIKONVERSION' ),


    
                            ])
                        ])
                    }
                }
            }




    //       /*    stage('download WAVE Artifacts from BAMS') {
    //             when {     
    //                   environment name: 'Component', value: 'Wave'                 
    //                 } 
    //                 steps {
    //                         powershell '''

    //                         [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    //                         #$ArtifactoryKey = "AKCp5btLFi3bQkZ59mgo2DyL3TL3SCK7TSgq4zfDme48mttXZpYtBasDBYwBtirbsctXcMYtz"
    //                         $localArtifactPath = "$env:Downlaodlocation//$env:ZIPVersion.zip" 
    //                         Invoke-WebRequest -Method Get -Uri "https://bams-aws.refinitiv.com/artifactory/default.nuget.cloud/nawm/WeatherAdvisors/Wave/$env:SourceEnvironment/$env:ZIPVersion.zip" -OutFile $localArtifactPath -Headers @{"X-JFrog-Art-Api"=$env:BAMS_CREDS}

    //                         ''' 
                        
    //         }*/
        
    //     stage('download WAVE-NG Artifacts from BAMS') {
    //         when {     
    //                 environment name: 'Component', value: 'Waveng'                 
    //             }  
    //                 steps {
    //                         powershell '''

    //                         write-host "source: $env:SourceEnvironment"
    //                         write-host "target: $env:TargetEnvironment"
    //                         [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    //                         #$ArtifactoryKey = "AKCp5btLFi3bQkZ59mgo2DyL3TL3SCK7TSgq4zfDme48mttXZpYtBasDBYwBtirbsctXcMYtz"
    //                         $localArtifactPath = "$env:Downlaodlocation//$env:ZIPVersion.zip" 
    //                         Invoke-WebRequest -Method Get -Uri "https://bams-aws.refinitiv.com/artifactory/default.nuget.cloud/nawm/WeatherAdvisors/Waveng/$env:SourceEnvironment/$env:ZIPVersion.zip" -OutFile $localArtifactPath -Headers @{"X-JFrog-Art-Api"=$env:BAMS_CREDS}

    //                         ''' 
                        
    //         }
    //     }
    //    stage('UnZip'){  
    //             steps{
    //                 powershell '''
    //                 Add-Type -AssemblyName System.IO.Compression.FileSystem
    //                 $zipfile = "$env:Downlaodlocation//$env:ZIPVersion.zip"
    //                 $outpath =  "$env:ExtractToDirectory"
    //                 [System.IO.Compression.ZipFile]::ExtractToDirectory($zipfile, $outpath)

    //              '''  
    //             }
                           
    //     }

    //    /* stage('Transfermation'){  
    //             steps{
    //                 powershell '''
    //                 Write-Host "Copying Configs"
    //                 $src_dir = "$env:TransPath"
    //                 $dst_dir = "$env:ExtractToDirectory"
    //                 Copy-Item $src_dir//*.* $dst_dir -recurse

    //              '''  
    //             }
                           
    //     }*/
       
    //     stage('Version') {
           
    //         steps {
    //             powershell '''
                
    //             $version = "$env:Eikonversion"
    //             Write-Output "$version"
    //             $pathToJson = "$env:AppJson"
    //             Write-Output "$pathToJson"
    //             $a = Get-Content $pathToJson | Out-String | ConvertFrom-Json
    //             $a.version = $version
    //             $a | ConvertTo-Json | set-content $pathToJson
                
    //             '''
    //         }
    //     }
    //     stage('HTMLVersion'){
    //         steps{
    //             powershell '''

    //             $path = "$env:Indexfilepath"
    //             Write-Output "HTMLPath: $path"
    //             $version = "$env:Eikonversion"
    //             Write-Output "HTMLVersion: $version"
    //             $zipversion = "$env:ZIPVersion"
    //             Write-Output "HTMLZIPVersion: $zipversion"
    //             try{
    //                     (Get-Content $path) -replace $zipversion, $version | Set-Content $path
    //             }
    //             catch{
    //                 Write-Output "Replace not happened"
    //                 Write-Host $_
    //             }
    //              Write-Output "Replace done"  

    //             '''
    //         }
    //     }
    //    stage('BETA_Publish') {

    //         when {     
    //                   environment name: 'TransEnvs', value: 'Alpha to Beta'                 
    //                 } 

    //         steps {
    //             dir("${env.WORKSPACE}") {
    //                 bat '%Eikon%/eem_deploy.exe ppe1hdccispod apppackage %ExtractToDirectory% /N:%Appname% /S:true'
    //             }
    //         }
    //     }
    //     stage('PROD_hdcphdccpspod_Publish') {
    //         when {     
    //                   environment name: 'TransEnvs', value: 'Beta to Prod'                 
    //                 } 
    //         steps {
    //             dir("${env.WORKSPACE}") {
    //                 bat '%Eikon%/eem_deploy.exe hdcphdccpspod apppackage %ExtractToDirectory% /N:%Appname% /S:true'
    //             }
    //         }
    //     }
    //     stage('PROD_ntcpntccpspod_Publish') {
    //         when {     
    //                   environment name: 'TransEnvs', value: 'Beta to Prod'                 
    //                 } 
    //         steps {
    //             dir("${env.WORKSPACE}") {
    //                 bat '%Eikon%/eem_deploy.exe ntcpntccpspod apppackage %ExtractToDirectory% /N:%Appname% /S:true'
    //             }
    //         }
    //     }
    //     stage('PROD_stcpstccpspod_Publish') {
    //         when {     
    //                   environment name: 'TransEnvs', value: 'Beta to Prod'                 
    //                 } 
    //         steps {
    //             dir("${env.WORKSPACE}") {
    //                 bat '%Eikon%/eem_deploy.exe stcpstccpspod apppackage %ExtractToDirectory% /N:%Appname% /S:true'
    //             }
    //         }
    //     }
    //     stage('PROD_dtcpdtccpspod_Publish') {
    //         when {     
    //                   environment name: 'TransEnvs', value: 'Beta to Prod'                 
    //                 } 
    //         steps {
    //             dir("${env.WORKSPACE}") {
    //                 bat '%Eikon%/eem_deploy.exe dtcpdtccpspod apppackage %ExtractToDirectory% /N:%Appname% /S:true'
    //             }
    //         }
    //     }
      /*   stage('Zip Target Artifacts') { 
            steps {
                    powershell '''
                        
                        If(Test-path $env:ArtifactsPath) {Remove-item $env:ArtifactsPath}
                        Add-Type -assembly "system.io.compression.filesystem"
                        [io.compression.zipfile]::CreateFromDirectory($env:ExtractToDirectory, $env:ArtifactsPath)
                    '''
                
            }
        } 
        
        stage('Upload Target Artifacts to BAMS') {
            
                    steps {
                            powershell '''
                                
                                [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
                                $URI = "$env:Artifactdownloadurl//$env:Component//$env:TargetEnvironment//$env:ZIPVersion.zip"  
                                Invoke-WebRequest -Uri $URI -InFile $env:ArtifactsPath -Method Put -Header @{"X-JFrog-Art-Api"=$env:BAMS_CREDS}

                            '''
        
                        }
        }  */

        }//end of stages
    } //end of pipeline
}