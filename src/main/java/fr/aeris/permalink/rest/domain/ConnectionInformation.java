package fr.aeris.permalink.rest.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionInformation {

	private String token;
	private List<Role> roles;
	private String name;
	private String orcid;
}