package fr.aeris.permalink.rest.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import fr.aeris.permalink.rest.config.Profiles;
import fr.aeris.permalink.rest.domain.Permalink;

@Component
@Profile("!"+Profiles.FAKE_PROFILE)
public class PermalinkDaoMongoImpl extends AbstractPermalinkDao{

	@Autowired
	PermalinkRepository permalinkRepository;
	
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
	
	
}
