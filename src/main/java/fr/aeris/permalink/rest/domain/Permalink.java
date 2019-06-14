package fr.aeris.permalink.rest.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = Permalink.PERMALINK_COLLECTION_NAME, language = "english")
public class Permalink {
	
	public final static String PERMALINK_COLLECTION_NAME = "permalinks";
	
	@Id
	private String id;
	
	@Indexed(unique = true)
	private String suffix;
	
	private String url;
	private List<String> managerIds;
	
	public void setSuffix (String suffix) {
		this.suffix = suffix.trim().toLowerCase();
	}
	
	public boolean isManagedBy(String orcid) {
		if (managerIds == null) {
			return false;
		}
		else {
			for (String id : managerIds) {
				if (id.equalsIgnoreCase(orcid)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public void deleteManager(String orcid) {
		if (managerIds != null) {
			managerIds.remove(orcid);
		}
	}

	public void addManager(String orcid) {
		if (managerIds == null) {
			managerIds = new ArrayList<>();
		}
		for (String id : managerIds) {
			if (id.equalsIgnoreCase(orcid)) {
				return;
			}
		}
		
		managerIds.add(orcid.toLowerCase());
	}

}
