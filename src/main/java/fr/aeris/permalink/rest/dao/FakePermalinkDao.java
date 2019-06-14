package fr.aeris.permalink.rest.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import fr.aeris.permalink.rest.config.Profiles;
import fr.aeris.permalink.rest.domain.Permalink;
import fr.aeris.permalink.rest.domain.Statistics;

@Component
@Profile(Profiles.FAKE_PROFILE)
public class FakePermalinkDao extends AbstractPermalinkDao{

	List<Permalink> permalinks = new ArrayList<>();
	
	public FakePermalinkDao() {
		permalinks.add(create("google", "http://www.google.com","1234"));
		permalinks.add(create("monde", "http://www.lemonde.fr","12"));
		permalinks.add(create("equipe", "http://www.lequipe.fr","12"));
		permalinks.add(create("iagos", "http://www.iagos-data.fr/","0000-0001-6935-1106"));
		
	}
	
	@Override
	public List<Permalink> findAll() {
		return permalinks;
	}

	@Override
	public boolean redirects(String suffix) {
		for (Permalink permalink : permalinks) {
			if (permalink.getSuffix().equalsIgnoreCase(suffix)) {
				return true;
			}
		}
		return false;
	}
	
	private Permalink create(String suffix, String url, String... orcids ) {
		Permalink permalink = new Permalink();
		permalink.setSuffix(suffix);
		permalink.setUrl(url);
		ArrayList<String> managers = new ArrayList<>();
		for (String orcid : orcids) {
			managers.add(orcid);
		}
		permalink.setManagerIds(managers);
		return permalink;
	}

	@Override
	public Permalink findBySuffix(String suffix) {
		for (Permalink permalink : permalinks) {
			if (permalink.getSuffix().equalsIgnoreCase(suffix)) {
				return permalink;
			}
		}
		return null;
	}

	@Override
	public List<Permalink> findAllByOrcid(String orcid) {
		List<Permalink> result = new ArrayList<>();
		for (Permalink permalink : permalinks) {
			if (permalink.getManagerIds() != null) {
				if (permalink.getManagerIds().contains(orcid)) {
					result.add(permalink); 
				}
			}
		}
		return result;
	}

	@Override
	public void deleteBySuffix(String suffix) {
		List<Permalink> newPermalinks = new ArrayList<>();
		for (Permalink permalink : permalinks) {
			if (!permalink.getSuffix().equalsIgnoreCase(suffix)) {
				newPermalinks.add(permalink);
			}
		}
		this.permalinks = newPermalinks;
	}

	@Override
	public void save(Permalink permalink) {
		Permalink findBySuffix = findBySuffix(permalink.getSuffix());
		if (findBySuffix != null) {
			deleteBySuffix(permalink.getSuffix());
		}
		permalinks.add(permalink);
		
	}
	
	@Override
	public Statistics getStatistics() {
		Statistics result = new Statistics();
		Set<String> orcids = new HashSet<>();
		int permalinkNumber = 0;
		for (Permalink permalink : permalinks) {
			permalinkNumber++;
			for (String manager : permalink.getManagerIds()) {
				orcids.add(manager);
			}
		}
		result.setPermalinks(permalinkNumber);
		result.setUsers(orcids.size());
		return result;
	}

	

}
