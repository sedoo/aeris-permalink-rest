package fr.aeris.permalink.rest.actuator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.aeris.permalink.rest.dao.PermalinkDao;
import fr.aeris.permalink.rest.domain.Permalink;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UrlAvailableIndicator implements HealthIndicator {
        
    @Autowired
    PermalinkDao permalinkDao;
        
    @Override
    public Health health() {
    	log.info("Computing UrlAvailableIndicator");
    	List<Permalink> findAll = permalinkDao.findAll();
    	boolean isUp = true;
    	for (Permalink permalink : findAll) {
    		String url = permalink.getUrl();
    		if (StringUtils.isEmpty(StringUtils.trimToEmpty(url))) {
    			isUp = false;
    			break;
    		}
    		RestTemplate restTemplate = new RestTemplate();
    		try {
    			HttpHeaders headers = new HttpHeaders();

    			headers.add("user-agent", "Mozilla/5.0 Firefox/26.0");
    			HttpEntity<String> entity = new HttpEntity<>("", headers);
    			HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
    			
    			restTemplate.exchange(url, HttpMethod.HEAD, requestEntity, String.class);
    		} catch (Exception e) {
    			log.error("UNAVAILABLE URL: "+url+ "for suffix "+permalink.getSuffix());
    			isUp = false;
    			break;
    		}
		}
    	
    	
        if (!isUp) {
            return Health.down().withDetail("all url accessible", "No").build();
        }
        return Health.up().withDetail("all url accessible", "Yes").build();
    }

}