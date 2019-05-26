package fr.aeris.permalink.rest.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

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

	/**
	 * 
	 * @param request
	 * @return null user if no correct information is available
	 */
	public ApplicationUser getUserFromAuthHeader(HttpServletRequest request) {
		try{
			String token;
			try {
				token = JwtUtil.getTokenFromAuthHeader(request);
			}
			catch (JwtException e) {
				return null;
			}
			Claims claims = JwtUtil.getClaims(token, jwtConfig.getSigningKey());

			String orcid = claims.get(ORCID_KEY, String.class);
			String name = claims.getSubject();
			if (orcid != null){
				List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
				if (adminDao.isAdmin(orcid)) {
					authorities.add(new SimpleGrantedAuthority(Roles.ADMIN_ROLE));	
				} else {
					authorities.add(new SimpleGrantedAuthority(Roles.MANAGER_ROLE));
				}
				
				return new ApplicationUser(orcid, name, authorities);
			}
		}catch(Exception e){
			LOG.error("error reading token. Cause: " + e.getMessage());
		}	
		return null;
	}

	@Override
	public void doFilter(ServletRequest request,
			ServletResponse response,
			FilterChain filterChain)
					throws IOException, ServletException {

		LOG.debug("doFilter(");
		
		ApplicationUser user = getUserFromAuthHeader((HttpServletRequest)request);
				
		Authentication authentication = null;
		if (user != null){
			authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request,response);
	}
}