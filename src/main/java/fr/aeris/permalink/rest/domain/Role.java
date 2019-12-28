package fr.aeris.permalink.rest.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role {

	private String name;

	public Role() {

	}

	public Role(String name) {
		this.name = name;
	}

}
