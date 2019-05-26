package fr.aeris.permalink.rest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import fr.aeris.permalink.rest.dao.CachedPermalinkDao;
import fr.aeris.permalink.rest.dao.PermalinkDao;

@Configuration
public class DaoConfiguration {
	
	@Autowired
	PermalinkDao dao;
	
	@Bean 
	@Primary
	PermalinkDao getDao() {
		return new CachedPermalinkDao(dao);
	}
	
}
