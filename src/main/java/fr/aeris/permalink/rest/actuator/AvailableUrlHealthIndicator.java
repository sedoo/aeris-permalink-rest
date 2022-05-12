package fr.aeris.permalink.rest.actuator;

import java.util.ArrayList;
import java.util.Date;
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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Getter
@Setter
public class AvailableUrlHealthIndicator implements HealthIndicator {
        
    @Autowired
    PermalinkDao permalinkDao;
    
    private Date lastCheck;
    private List<Permalink> unavailablePermalinks = new ArrayList<>();
        
    @Override
    public Health health() {
    	log.info("Computing AvailableUrlHealthIndicator");
    	lastCheck = new Date();
    	unavailablePermalinks = new ArrayList<>();
    	List<Permalink> findAll = permalinkDao.findAll();
    	for (Permalink permalink : findAll) {
    		String url = permalink.getUrl();
    		if (StringUtils.isEmpty(StringUtils.trimToEmpty(url))) {
    			unavailablePermalinks.add(permalink);
    			continue;
    		}
    		RestTemplate restTemplate = new RestTemplate();
    		try {
    			HttpHeaders headers = new HttpHeaders();
    			//We indicate a User Agent to avoid 403 on wordpress pages 
    			headers.add("user-agent", "Mozilla/5.0 Firefox/26.0");
    			HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
    			restTemplate.exchange(url, HttpMethod.HEAD, requestEntity, String.class);
    		} catch (Exception e) {
    			unavailablePermalinks.add(permalink);
    			log.error("UNAVAILABLE URL: "+url+ "for suffix "+permalink.getSuffix());
    		}
		}
    	
    	
        if (unavailablePermalinks.size()>0) {
            return Health.down().withDetail("all url accessible", "No").build();
        }
        return Health.up().withDetail("all url accessible", "Yes").build();
    }

}