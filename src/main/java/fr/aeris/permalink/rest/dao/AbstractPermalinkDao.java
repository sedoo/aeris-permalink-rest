package fr.aeris.permalink.rest.dao;

import fr.aeris.permalink.rest.domain.Permalink;

public abstract class AbstractPermalinkDao implements PermalinkDao{
	
	@Override
	public boolean isSuffixAvailable(String suffix) {
		Permalink permalink = findBySuffix(suffix);
		if (permalink == null) {
			return true;
		}
		else {
			return false;
		}
	}

}
