package product.shortly.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import product.shortly.utils.BasicUtils;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.regions.Region;

@Configuration
public class DynamoDBConfiguration {
	Logger logger = LoggerFactory.getLogger(DynamoDBConfiguration.class);
	
//	@Value("${aws.region}")
//	private String awsRegion;
	
//	@Value("${aws.dynamodb.endpoint}")
//	private String dynamodbEndpoint;
//
//	@Value("${aws.dynamodb.accessKey}")
//	private String dynamodbAccessKey;
//
//	@Value("${aws.dynamodb.secretKey}")
//	private String dynamodbSecretKey;

	@Bean
	public DynamoDBMapper dynamoDBMapper() throws Exception {
		return new DynamoDBMapper(buildAmazonDynamoDB());
	}

	private AmazonDynamoDB buildAmazonDynamoDB() throws Exception {
		Map<String, String> map = BasicUtils.getEnvVariable();
	 
		com.amazonaws.regions.Region currentRegion = Regions.getCurrentRegion();
		String awsRegion =currentRegion == null ? "us-east-2": currentRegion.getName();
		String dynamodbEndpoint = map.get("aws.dynamodb.endpoint");
		String dynamodbAccessKey = map.get("aws.dynamodb.accessKey");
		String dynamodbSecretKey = map.get("aws.dynamodb.secretKey");

		return AmazonDynamoDBClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(dynamodbEndpoint, awsRegion))
				.withCredentials(
						new AWSStaticCredentialsProvider(new BasicAWSCredentials(dynamodbAccessKey, dynamodbSecretKey)))
				.build();
	}

	public Map<String, String> getSecret() throws Exception {

		String secretName = "dev/shortly-core/dynamo";
		Region region = Region.of("us-east-2");
		SecretsManagerClient client = SecretsManagerClient.builder().region(region).build();

		GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder().secretId(secretName).build();

		Map<String, String> map = new HashMap<>();

		GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
		String secret = getSecretValueResponse.secretString();
		new ObjectMapper().readValue(secret, Map.class);

		return map;
	}

}
