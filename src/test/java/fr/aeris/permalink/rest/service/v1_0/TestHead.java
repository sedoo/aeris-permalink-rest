package fr.aeris.permalink.rest.service.v1_0;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class TestHead {
	
	public static void main(String[] args) {
		RestTemplate template = new RestTemplate();
		try {
			
			HttpHeaders headers = new HttpHeaders();

			headers.add("user-agent", "Mozilla/5.0 Firefox/26.0");
			HttpEntity<String> entity = new HttpEntity<>("", headers);
			HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
			
			String url = "https://www4.obs-mip.fr/wp-content-omp/uploads/sites/21/2019/07/Parameter_Naming_Conventions-WML-08FEB2018ba.docx";
			template.exchange(url, HttpMethod.HEAD, requestEntity, String.class);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
