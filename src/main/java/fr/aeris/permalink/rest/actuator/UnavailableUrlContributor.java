package fr.aeris.permalink.rest.actuator;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import fr.aeris.permalink.rest.domain.Permalink;

@Component
public class UnavailableUrlContributor implements InfoContributor {

	@Autowired
	AvailableUrlHealthIndicator healthIndicator;

	@Override
	public void contribute(Builder builder) {

		Map<String, Object> details = new HashMap<>();

		String date ="-"; 
		if (healthIndicator.getLastCheck() != null) {
			SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			date = formater.format(healthIndicator.getLastCheck());
		}
		details.put("last check", date);
		
		Map<String, String> urls = new HashMap<>();
		
		if (healthIndicator.getUnavailablePermalinks().size()>0) {
			for (Permalink	permalink: healthIndicator.getUnavailablePermalinks()) {
				urls.put(permalink.getSuffix(), permalink.getUrl());
			}
			details.put("broken permalinks", urls);
		} else {
			details.put("broken permalinks", "None");
		}
		
		
		
		builder.withDetail("Unavailable Urls", details);

	}
}