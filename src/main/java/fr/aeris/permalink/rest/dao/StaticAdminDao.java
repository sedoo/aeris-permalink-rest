package fr.aeris.permalink.rest.dao;

import org.springframework.stereotype.Component;

@Component
public class StaticAdminDao implements AdminDao {

	private final String FRANCOIS_ANDRE_ORCID = "0000-0002-2275-0537";
	
	@Override
	public boolean isAdmin(String orcid) {
		return orcid.equals(FRANCOIS_ANDRE_ORCID);
	}

}
