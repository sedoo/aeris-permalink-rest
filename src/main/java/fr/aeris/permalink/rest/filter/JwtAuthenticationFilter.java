package fr.aeris.permalink.rest.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import fr.aeris.permalink.rest.dao.AdminDao;
import fr.aeris.permalink.rest.filter.jwt.JwtConfig;
import fr.aeris.permalink.rest.filter.jwt.JwtUtil;
import fr.aeris.permalink.rest.habilitation.ApplicationUser;
import fr.aeris.permalink.rest.habilitation.Roles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@Component
public class JwtAuthenticationFilter extends GenericFilterBean {

	private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	public final static String ORCID_KEY = "orcid";

	@Autowired
	AdminDao adminDao;

	@Autowired
	JwtConfig jwtConfig;

	public JwtAuthenticationFilter() {
		this.jwtConfig = new JwtConfig();
	}

	public JwtAuthenticationFilter(JwtConfig jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	private void extendTokenValidity(String token, HttpServletResponse response) {
		response.addHeader(JwtUtil.AUTH_HEADER, token);
	}

	private String generateToken(String userName, Set<String> roles, String orcid) throws Exception {
		Map<String, String> infos = new HashMap<>();
		infos.put(ORCID_KEY, orcid);
		String token = JwtUtil.generateToken(userName, jwtConfig.getSigningKey(), jwtConfig.getTokenValidity(), roles,
				infos);
		return token;
	}

	/**
	 * 
	 * @param request
	 * @return null user if no correct information is available
	 */
	public ApplicationUser getUserFromAuthHeader(HttpServletRequest request, HttpServletResponse response) {
		try {

			ApplicationUser loggedUser = null;
			try {
				loggedUser = LoginUtils.getLoggedUser();
			} catch (Exception e) {

			}
			if (loggedUser != null) {
				return loggedUser;
			} else {
				String token;
				try {
					token = JwtUtil.getTokenFromAuthHeader(request);
				} catch (JwtException e) {
					return null;
				}
				Claims claims = JwtUtil.getClaims(token, jwtConfig.getSigningKey());

				String orcid = claims.get(ORCID_KEY, String.class);
				String name = claims.getSubject();
				Set<String> roles = new HashSet<>();
				if (orcid != null) {
					List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
					if (adminDao.isAdmin(orcid)) {
						authorities.add(new SimpleGrantedAuthority(Roles.ADMIN_ROLE));
						roles.add(Roles.ADMIN_ROLE);
					} else {
						authorities.add(new SimpleGrantedAuthority(Roles.MANAGER_ROLE));
						roles.add(Roles.MANAGER_ROLE);
					}
					extendTokenValidity(generateToken(name, roles, orcid), response);
					return new ApplicationUser(orcid, name, authorities);
				}
			}
		} catch (Exception e) {
			LOG.error("error reading token. Cause: " + e.getMessage());
		}
		return null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		LOG.debug("doFilter(");

		ApplicationUser user = getUserFromAuthHeader((HttpServletRequest) request, (HttpServletResponse) response);

		Authentication authentication = null;
		if (user != null) {
			authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}
}