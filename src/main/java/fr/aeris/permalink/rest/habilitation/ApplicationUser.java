package fr.aeris.permalink.rest.habilitation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class ApplicationUser extends User {

	private String name;
	private String orcid;
	private Set<String> roles;

	public ApplicationUser(String orcid) {
		super(orcid, "not_applicable", new ArrayList<>());
		this.orcid = orcid;
	}

	public ApplicationUser(String orcid, String name, Collection<? extends GrantedAuthority> authorities) {
		super(orcid, "not_applicable", authorities);
		this.orcid = orcid;
		this.name = name;
		this.roles = new HashSet<String>();
		for (GrantedAuthority a : authorities) {
			roles.add(a.getAuthority());
		}
	}

	public String getOrcid() {
		return getUsername();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addRole(String role) {
		roles.add(role);
		getAuthorities().add(new SimpleGrantedAuthority(role));
	}

	public Set<String> getRoles() {
		return roles;
	}

	public boolean isAdmin() {
		return hasRole(Roles.ADMIN_ROLE);
	}

	public boolean hasRole(String role) {
		return getAuthorities().contains(new SimpleGrantedAuthority(role));
	}

}
