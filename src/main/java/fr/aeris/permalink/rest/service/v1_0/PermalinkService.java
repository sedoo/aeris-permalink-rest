package fr.aeris.permalink.rest.service.v1_0;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.aeris.permalink.rest.dao.PermalinkDao;
import fr.aeris.permalink.rest.domain.Offer;
import fr.aeris.permalink.rest.domain.Permalink;
import fr.aeris.permalink.rest.domain.Statistics;
import fr.aeris.permalink.rest.habilitation.ApplicationUser;
import fr.aeris.permalink.rest.habilitation.Roles;
import fr.aeris.permalink.rest.service.v1_0.exception.BadRequestException;
import fr.aeris.permalink.rest.service.v1_0.exception.ForbiddenException;
import fr.aeris.permalink.rest.service.v1_0.exception.ServiceOfferLimitReachedException;
import fr.aeris.permalink.rest.service.v1_0.exception.UnexistingPermalinkException;


@RestController
@CrossOrigin
@RequestMapping(value = "/admin/v1_0")
public class PermalinkService {

	private static final Logger LOG = LoggerFactory.getLogger(PermalinkService.class);

	@Autowired
	PermalinkDao permalinkDao;

	@RequestMapping(value = "/isalive", method = RequestMethod.GET)
	public String isalive() {
		return "yes";
	}
	
	@RequestMapping(value = "/statistics", method = RequestMethod.GET)
	public Statistics statistics() {
		
		return permalinkDao.getStatistics();
	}
	
	@RequestMapping(value = "/offer", method = RequestMethod.GET)
	public Offer offer() {
		Offer result = new Offer();
		result.setMaxPermalinks(PermalinkDao.USER_PERMALINK_LIMITS);
		return result;
	}

	@Secured({Roles.ADMIN_ROLE, Roles.MANAGER_ROLE})
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<Permalink> listall(@RequestHeader("Authorization") String authHeader, @AuthenticationPrincipal ApplicationUser user) {
		if (user.isAdmin()) {
			return permalinkDao.findAll();
		}
		else {
			String orcid = user.getOrcid();
			return permalinkDao.findAllByOrcid(orcid);
		}
	}

	@Secured({Roles.ADMIN_ROLE, Roles.MANAGER_ROLE})
	@RequestMapping(value = "/delete/{suffix}", method = RequestMethod.DELETE)
	public void delete(@RequestHeader("Authorization") String authHeader, @AuthenticationPrincipal ApplicationUser user, @PathVariable(name="suffix") String suffix) {
		Permalink permalink = permalinkDao.findBySuffix(suffix);
		if (permalink == null) {
			throw new UnexistingPermalinkException();
		}
		if (user.isAdmin()) {
			permalinkDao.deleteBySuffix(suffix);
		}
		else {
			if (permalink.getManagerIds() == null) {
				throw new ForbiddenException();
			}
			String orcid = user.getOrcid();
			if (permalink.isManagedBy(orcid)) {
				permalinkDao.deleteBySuffix(suffix);
			}
		}
	}

	@Secured({Roles.ADMIN_ROLE, Roles.MANAGER_ROLE})
	@RequestMapping(value = "/addmanager/{suffix}/{orcid}", method = RequestMethod.GET)
	public void addManager(@RequestHeader("Authorization") String authHeader, @AuthenticationPrincipal ApplicationUser user, @PathVariable(name="suffix") String suffix, @PathVariable(name="orcid") String orcid) {
		if ((StringUtils.isEmpty(suffix)) || (StringUtils.isEmpty(orcid))) {
			throw new BadRequestException();
		}
		String userOrcid = user.getOrcid();
		Permalink existingPermalink = permalinkDao.findBySuffix(suffix);
		if (existingPermalink != null) {
			if (existingPermalink.isManagedBy(userOrcid)) {
				existingPermalink.addManager(orcid);
				permalinkDao.save(existingPermalink);
			}
			else if (user.isAdmin()) {
				existingPermalink.addManager(orcid);
				permalinkDao.save(existingPermalink);
			}
			else {
				throw new ForbiddenException();
			}
		}
		else {
			throw new UnexistingPermalinkException();
		}
	}
	
	@Secured({Roles.ADMIN_ROLE, Roles.MANAGER_ROLE})
	@RequestMapping(value = "/deletemanager/{suffix}/{orcid}", method = RequestMethod.GET)
	public void deleteManager(@RequestHeader("Authorization") String authHeader, @AuthenticationPrincipal ApplicationUser user, @PathVariable(name="suffix") String suffix, @PathVariable(name="orcid") String orcid) {
		if ((StringUtils.isEmpty(suffix)) || (StringUtils.isEmpty(orcid))) {
			throw new BadRequestException();
		}
		String userOrcid = user.getOrcid();
		Permalink existingPermalink = permalinkDao.findBySuffix(suffix);
		if (existingPermalink != null) {
			if (existingPermalink.isManagedBy(userOrcid)) {
				existingPermalink.deleteManager(orcid);
				permalinkDao.save(existingPermalink);
			}
			else {
				throw new ForbiddenException();
			}
		}
		else {
			throw new UnexistingPermalinkException();
		}
	}
	
	
	@Secured({Roles.ADMIN_ROLE, Roles.MANAGER_ROLE}) 
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public void add(@RequestHeader("Authorization") String authHeader, @AuthenticationPrincipal ApplicationUser user, @RequestBody Permalink permalink) {
		if ((permalink == null) || (StringUtils.isEmpty(permalink.getSuffix())) || (StringUtils.isEmpty(permalink.getUrl()))) {
			throw new BadRequestException();
		}
		String orcid = user.getOrcid();
		Permalink existingPermalink = permalinkDao.findBySuffix(permalink.getSuffix());
		if (existingPermalink != null) {
			if (existingPermalink.isManagedBy(orcid)) {
				existingPermalink.setUrl(permalink.getUrl());
				permalinkDao.save(existingPermalink);
			}
			else {
				throw new ForbiddenException();
			}
		}
		else {
			List<Permalink> permalinks = permalinkDao.findAllByOrcid(orcid);
			if (permalinks.size() >= PermalinkDao.USER_PERMALINK_LIMITS) {
				throw new ServiceOfferLimitReachedException();
			}	
			else {
				permalink.addManager(orcid);
				permalinkDao.save(permalink);
			}
		}
	}
	
	
	


}



