package fr.aeris.permalink.rest.actuator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import fr.aeris.permalink.rest.dao.PermalinkDao;
import fr.aeris.permalink.rest.domain.Statistics;

@Component
public class StatisticsInfoContributor implements InfoContributor {

	@Autowired
	PermalinkDao permalinkDao;

	@Override
	public void contribute(Builder builder) {
		Statistics statistics = permalinkDao.getStatistics();

		Map<String, String> details = new HashMap<>();

		details.put("users", "" + statistics.getUsers());
		details.put("permalinks", "" + statistics.getPermalinks());
		builder.withDetail("statistics", details);

	}
}