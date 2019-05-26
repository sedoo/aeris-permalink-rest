package fr.aeris.permalink.rest.dao;

import java.util.ArrayList;
import java.util.List;

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
		return false;
	}

	@Override
	public Permalink findBySuffix(String suffix) {
		return permalinkRepository.findBySuffix(suffix);
	}

	@Override
	public List<Permalink> findAllByOrcid(String orcid) {
		return new ArrayList<>();
	}

	@Override
	public void deleteBySuffix(String suffix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(Permalink permalink) {
		// TODO Auto-generated method stub
		
	}
	
	
}
