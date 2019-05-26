package fr.aeris.permalink.rest.dao;

import java.util.List;

import fr.aeris.permalink.rest.domain.Permalink;

public interface PermalinkDao {
	
	int USER_PERMALINK_LIMITS =50;

	List<Permalink> findAll();

	boolean redirects(String suffix);
	
	Permalink findBySuffix(String suffix);
	
	boolean isSuffixAvailable(String suffix);

	List<Permalink> findAllByOrcid(String orcid);

	void deleteBySuffix(String suffix);
	
	void save(Permalink permalink);

}
