package product.shortly.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BasicUtils {
	private static Map<String, String> envVariables;
	private static Object lock = new Object();
	private static Logger logger = LoggerFactory.getLogger(BasicUtils.class);
	public static Map<String, String> getEnvVariable() {
		if (envVariables == null) {
			synchronized (lock) {
				if (envVariables == null) {
					envVariables = new HashMap<>();
					envVariables.put("aws.dynamodb.accessKey", System.getProperty("aws_dynamodb_accessKey"));
					envVariables.put("aws.dynamodb.secretKey", System.getProperty("aws_dynamodb_secretKey"));
					envVariables.put("aws.dynamodb.endpoint", System.getProperty("aws_dynamodb_endpoint"));
					envVariables.put("shortURL", System.getProperty("shortURL"));
					envVariables.entrySet().stream().forEach(e -> logger.info(e.getKey()+":"+e.getValue()));
				}
			}
		}
		return envVariables;
	}
}