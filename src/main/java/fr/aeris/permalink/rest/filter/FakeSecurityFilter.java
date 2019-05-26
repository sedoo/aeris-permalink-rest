package fr.aeris.permalink.rest.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import fr.aeris.permalink.rest.habilitation.ApplicationUser;
import fr.aeris.permalink.rest.habilitation.Roles;

//@Component
//@Order(2)
public class FakeSecurityFilter extends GenericFilterBean {

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//SecurityContextHolder.getContext().setAuthentication(authentication);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		
		List<String> roles = new ArrayList<>();
		roles.add(Roles.ADMIN_ROLE);
		
		for (String role: roles){
			authorities.add(new SimpleGrantedAuthority(role));
		}
		ApplicationUser user = new ApplicationUser("xx", "yy", authorities);

		Authentication authentication = null;
		if (user != null){
			authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request,response);
	
	}

	

}
