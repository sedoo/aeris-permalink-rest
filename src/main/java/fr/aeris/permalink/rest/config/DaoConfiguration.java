package fr.aeris.permalink.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import fr.aeris.permalink.rest.dao.CachedPermalinkDao;
import fr.aeris.permalink.rest.dao.PermalinkDao;

@Configuration
public class DaoConfiguration {
	
	@Autowired
	PermalinkDao dao;
	
	@Bean 
	@Primary
	@Profile({Profiles.PRODUCTION_PROFILE, Profiles.FAKE_PROFILE})
	PermalinkDao getDao() {
		return new CachedPermalinkDao(dao);
	}
	
}
