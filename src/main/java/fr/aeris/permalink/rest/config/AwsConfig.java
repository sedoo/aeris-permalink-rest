package fr.aeris.permalink.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class AwsConfig {
	
	@Value("${backup.aws.accessKey}")
	private String accessKey;
	
	@Value("${backup.aws.secretKey}")
	private String secretKey;
	
	@Value("${backup.aws.bucketName}")
	private String bucketName;
	
	public AWSCredentials getCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}
	
}
