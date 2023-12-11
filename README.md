# shortly
The purpose of this service is to pre-generate range of alias that the controller serving the create alias and resolve alias can use. This logic can be put in the shortly service and executed during bootstrap time. However, if there is complex logic in pre-generating ranges then time to spin up EC2 will increase.

Full story [here](https://medium.com/@mail.ekansh/shortly-aws-implementation-d76bff78550e)


## Building application
Built using: 
Apache Maven 3.8.6
Java version: 17.0.5, vendor: Oracle Corporation
`mvn clean package -DskipTests`

## Local testing or running the jar
 Setup access key and secret key for Dynamo DB shortly DB. (See Expected exception when running locally)
 
```
cd {PROJECT_ROOT}/target
java -Daws_dynamodb_accessKey=${aws_dynamodb_accessKey} -Daws_dynamodb_secretKey=${aws_dynamodb_secretKey} -Daws_dynamodb_endpoint=https://dynamodb.us-east-2.amazonaws.com -DshortURL=http://localhost:8080/ -Dserver.port=8080 -jar shortly-0.0.1-SNAPSHOT.jar
```

### Create an short URL
Don't use Windows command line for CURL, it will give port number not correct error 

```
curl --location 'http://localhost:8080/short' \
--header 'Content-Type: application/json' \
--data '{"url": "https://www.google.com/search?q=helloworld"}'
```

Expected output : `{"originalURL":"https://www.google.com/search?q=helloworld","shortly":"http://localhost:8080/akaaa6P"}`

### Test the Short URL
Better to test the `shortly` value from above result on browser, but if you want to do it via CURL then do this:

```
curl --location 'http://localhost:8080/akaaa6P' \
--header 'Content-Type: application/json'
```

## Deployment on AWS

Ensure the EC2 instance is running in the correct vpc with the correct Security Groups.  For simple testing just spin the instance in public subnet and ensure public IP address  is assigned to it. Also make sure that you can SSH to the instance - you have pem key and port 22 is available. 

`ssh -i shortly.bastion.rds.pem ec2-user@3.15.236.0`

### install sdkman on AWS

`curl -s "https://get.sdkman.io" | bash`
`source "$HOME/.sdkman/bin/sdkman-init.sh"`
`sdk install java 17.0.9-amzn`

### Copy jar TO the ec2 instance from the local
`scp -i shortly.bastion.rds.pem C:\git\shortly\target\shortly-0.0.1-SNAPSHOT.jar ec2-user@3.15.236.0:/home/ec2-user`

### Run the Jar 
ensure shortURL and server.port when running locally or on ec2 instance
(see Local testing or running the jar)
example

```
java -Daws_dynamodb_accessKey=AKIAXYUWPYOTCBFY4BOG -Daws_dynamodb_secretKey=vAUsTyQDkHGNqMdkGuAYzI0oFPCGhKZPMPRCFnqJ -Daws_dynamodb_endpoint=https://dynamodb.us-east-2.amazonaws.com -DshortURL=http://ec2-3-15-236-0.us-east-2.compute.amazonaws.com:8080/ -Dserver.port=8080 -jar shortly-0.0.1-SNAPSHOT.jar
```



### Create an short URL
Don't use Windows command line for CURL

```
curl --location 'http://ec2-3-15-236-0.us-east-2.compute.amazonaws.com:8080/short' --header 'Content-Type: application/json' --data '{"url": "https://www.google.com/search?q=helloworld"}'

```


### Test the Short URL
Open this link in the browser : `"http://ec2-3-15-236-0.us-east-2.compute.amazonaws.com:8080/akaaa7L`


### Expected exception when running locally: 

```
2023-12-10 20:42:36.027  INFO 26192 --- [           main] product.shortly.utils.BasicUtils         : aws.dynamodb.endpoint:https://dynamodb.us-east-2.amazonaws.com
2023-12-10 20:42:44.729  WARN 26192 --- [           main] com.amazonaws.util.EC2MetadataUtils      : Unable to retrieve the requested metadata (/latest/dynamic/instance-identity/document). Failed to connect to service endpoint:

com.amazonaws.SdkClientException: Failed to connect to service endpoint:
        at com.amazonaws.internal.EC2ResourceFetcher.doReadResource(EC2ResourceFetcher.java:100) ~[aws-java-sdk-core-1.12.346.jar!/:na]
        at com.amazonaws.internal.EC2ResourceFetcher.doReadResource(EC2ResourceFetcher.java:70) ~[aws-java-sdk-core-1.12.346.jar!/:na]
        at com.amazonaws.internal.InstanceMetadataServiceResourceFetcher.readResource(InstanceMetadataServiceResourceFetcher.java:75) ~[aws-java-sdk-core-1.12.346.jar!/:na]
        at com.amazonaws.internal.EC2ResourceFetcher.readResource(EC2ResourceFetcher.java:66) ~[aws-java-sdk-core-1.12.346.jar!/:na]
        at com.amazonaws.util.EC2MetadataUtils.getItems(EC2MetadataUtils.java:407) ~[aws-java-sdk-core-1.12.346.jar!/:na]
        at com.amazonaws.util.EC2MetadataUtils.getData(EC2MetadataUtils.java:376) ~[aws-java-sdk-core-1.12.346.jar!/:na]
        at com.amazonaws.util.EC2MetadataUtils.getData(EC2MetadataUtils.java:372) ~[aws-java-sdk-core-1.12.346.jar!/:na]
        at com.amazonaws.util.EC2MetadataUtils.getEC2InstanceRegion(EC2MetadataUtils.java:287) ~[aws-java-sdk-core-1.12.346.jar!/:na]
        at com.amazonaws.regions.Regions.getCurrentRegion(Regions.java:113) ~[aws-java-sdk-core-1.12.346.jar!/:na]
        at product.shortly.config.DynamoDBConfiguration.buildAmazonDynamoDB(DynamoDBConfiguration.java:51) ~[classes!/:0.0.1-SNAPSHOT]
        at product.shortly.config.DynamoDBConfiguration.dynamoDBMapper(DynamoDBConfiguration.java:45) ~[classes!/:0.0.1-SNAPSHOT]
        at product.shortly.config.DynamoDBConfiguration$$EnhancerBySpringCGLIB$$f97f271a.CGLIB$dynamoDBMapper$0(<generated>) ~[classes!/:0.0.1-SNAPSHOT]
        at product.shortly.config.DynamoDBConfiguration$$EnhancerBySpringCGLIB$$f97f271a$$FastClassBySpringCGLIB$$150e8e6a.invoke(<generated>) ~[classes!/:0.0.1-SNAPSHOT]
 ```
 

