package fr.aeris.permalink.rest.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import fr.aeris.permalink.rest.dao.PermalinkDao;
import fr.aeris.permalink.rest.domain.Permalink;

@Component
@Order(1)
public class RedirectionFilter implements Filter {
	
	@Autowired
	PermalinkDao permalinkDao; 
	
	private final static Logger LOG = LoggerFactory.getLogger(RedirectionFilter.class);

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		LOG.info("Initializing filter :{}", this);
	}
	
	@Override
	public void destroy() {
		LOG.warn("Destructing filter :{}", this);
	}
 
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		HttpServletRequest r = (HttpServletRequest) request;
		String suffix = r.getRequestURI().substring(r.getContextPath().length());
		if (suffix.startsWith("/")) {
			suffix = suffix.substring(1);
		}
		LOG.info("Filtering for URL :{}", suffix);
		if (permalinkDao.redirects(suffix)) {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			Permalink permalink = permalinkDao.findBySuffix(suffix);
			LOG.info("Redirecting {} toward :{}", suffix, permalink.getUrl());
			httpResponse.sendRedirect(permalink.getUrl());
			return;
		} else {
			LOG.info("Suffix {} not redirected", suffix);
			chain.doFilter(request, response);
		}
	}

}
