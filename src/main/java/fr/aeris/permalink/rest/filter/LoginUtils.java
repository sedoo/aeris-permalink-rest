package fr.aeris.permalink.rest.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import fr.aeris.permalink.rest.exception.UserNotLoggedException;
import fr.aeris.permalink.rest.habilitation.ApplicationUser;

@Component
public class LoginUtils {
	private static final Logger LOG = LoggerFactory.getLogger(LoginUtils.class);

	public static ApplicationUser getLoggedUser() throws UserNotLoggedException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			LOG.debug("principal: " + authentication.getPrincipal());
			if (authentication.getPrincipal() instanceof ApplicationUser) {
				return (ApplicationUser) authentication.getPrincipal();
			}
		}
		throw new UserNotLoggedException();
	}

}
