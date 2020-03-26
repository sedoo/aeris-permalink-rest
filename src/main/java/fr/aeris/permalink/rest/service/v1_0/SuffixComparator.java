package fr.aeris.permalink.rest.service.v1_0;

import java.util.Comparator;

import fr.aeris.permalink.rest.domain.Permalink;

public class SuffixComparator implements Comparator<Permalink> {

	@Override
	public int compare(Permalink o1, Permalink o2) {
		return o1.getSuffix().compareToIgnoreCase(o2.getSuffix());
	}

}
