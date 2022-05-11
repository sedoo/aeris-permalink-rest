package fr.aeris.permalink.rest.actuator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import fr.aeris.permalink.rest.dao.PermalinkDao;
import fr.aeris.permalink.rest.domain.Permalink;

@Component
public class UrlAvailableIndicator implements HealthIndicator {
        private final String message_key = "Service A";
        
    @Autowired
    PermalinkDao permalinkDao;
        
    @Override
    public Health health() {
    	
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
    			restTemplate.headForHeaders(url);
    		} catch (Exception e) {
    			isUp = false;
    			break;
    		}
		}
    	
    	
        if (!isUp) {
            return Health.down().withDetail("all url accessible", "No").build();
        }
        return Health.up().withDetail("all url accessible", "Yes").build();
    }

    private Boolean isRunningServiceA() {
        Boolean isRunning = true;
        // Logic Skipped

        return isRunning;
    }
}