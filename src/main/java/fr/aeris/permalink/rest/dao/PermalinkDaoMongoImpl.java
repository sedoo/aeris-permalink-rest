package fr.aeris.permalink.rest.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.client.DistinctIterable;

import fr.aeris.permalink.rest.config.Profiles;
import fr.aeris.permalink.rest.domain.Permalink;
import fr.aeris.permalink.rest.domain.Statistics;

@Component
@Profile("!" + Profiles.FAKE_PROFILE)
public class PermalinkDaoMongoImpl extends AbstractPermalinkDao {

	@Autowired
	PermalinkRepository permalinkRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public List<Permalink> findAll() {
		return permalinkRepository.findAll();
	}

	@Override
	public boolean redirects(String suffix) {
		Permalink findBySuffix = findBySuffix(suffix);
		if (findBySuffix != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Permalink findBySuffix(String suffix) {
		return permalinkRepository.findBySuffix(suffix.toLowerCase());
	}

	@Override
	public List<Permalink> findAllByOrcid(String orcid) {
		return permalinkRepository.findByManagerIdsIn(Collections.singletonList(orcid));
	}

	@Override
	public void deleteBySuffix(String suffix) {
		Permalink existingPermalink = findBySuffix(suffix);
		if (existingPermalink != null) {
			permalinkRepository.delete(existingPermalink);
		}
	}

	@Override
	public void save(Permalink permalink) {
		permalink.setSuffix(permalink.getSuffix().toLowerCase());
		if (StringUtils.isEmpty(permalink.getSuffix())) {
			return;
		}
		if (StringUtils.isEmpty(permalink.getUrl())) {
			return;
		}
		deleteBySuffix(permalink.getSuffix());
		permalinkRepository.save(permalink);
	}

	@Override
	public Statistics getStatistics() {
		Statistics result = new Statistics();
		result.setPermalinks((int) permalinkRepository.count());
		DistinctIterable<String> aux = mongoTemplate.getCollection(Permalink.PERMALINK_COLLECTION_NAME)
				.distinct("managerIds", String.class);

		List<String> list = new ArrayList<>();
		aux.into(list);
		result.setUsers(list.size());
		return result;
	}

}
