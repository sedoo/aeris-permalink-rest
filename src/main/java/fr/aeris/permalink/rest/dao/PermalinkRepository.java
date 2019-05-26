package fr.aeris.permalink.rest.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.aeris.permalink.rest.domain.Permalink;

public interface PermalinkRepository extends MongoRepository<Permalink, String> {
	
	Permalink findBySuffix(String suffix);

}
