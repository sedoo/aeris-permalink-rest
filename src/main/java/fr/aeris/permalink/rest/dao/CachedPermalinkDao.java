package fr.aeris.permalink.rest.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;

import fr.aeris.permalink.rest.domain.Permalink;

public class CachedPermalinkDao extends AbstractPermalinkDao {

	private PermalinkDao dao;
	
	private List<String> suffixes = new ArrayList<>();

	private List<Permalink> permalinks = new ArrayList<>();

	public CachedPermalinkDao(PermalinkDao dao) {
		this.dao = dao;
	}

	@Override
	public List<Permalink> findAll() {
		return permalinks;
	}

	@Override
	public boolean redirects(String suffix) {
		return suffixes.contains(suffix.toLowerCase());
	}
	
	@PostConstruct() 
	private void init(){
		updateCache();
	}
	
	@Scheduled(fixedDelay = 3600000)
	private void refresh(){
		updateCache();
	}

	private void updateCache() {
		List<Permalink> findAll = dao.findAll();
		if (findAll == null) {
			findAll = new ArrayList<>();
		}
		List<String> newSuffixes = new ArrayList<>();
		for (Permalink permalink : findAll) {
			newSuffixes.add(permalink.getSuffix().toLowerCase());
		}
		suffixes = newSuffixes;
		permalinks  = findAll; 
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
		this.suffixes.remove(suffix);
		dao.deleteBySuffix(suffix);
	}

	@Override
	public void save(Permalink permalink) {
		if (suffixes.contains(permalink.getSuffix())) {
			deleteBySuffix(permalink.getSuffix());
		}
		permalinks.add(permalink);
		suffixes.add(permalink.getSuffix());
		dao.save(permalink);
		
	}

	
	
	
	
	
	

}
