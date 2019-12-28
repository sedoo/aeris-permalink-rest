package fr.aeris.permalink.rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="orcid")
public class OrcidConfig {

	private String clientId;
	private String clientSecret;
	private String tokenUrl;
	private String publicApiUrl;
	
	public OrcidConfig() {
		
	}
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}
	
	public String getPublicApiUrl() {
		return publicApiUrl;
	}
	public void setPublicApiUrl(String publicApiUrl) {
		this.publicApiUrl = publicApiUrl;
	}
	
	
}
