package fr.aeris.permalink.rest.service.v1_0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import fr.aeris.permalink.rest.config.OrcidConfig;
import fr.aeris.permalink.rest.dao.AdminDao;
import fr.aeris.permalink.rest.domain.ConnectionInformation;
import fr.aeris.permalink.rest.domain.OrcidToken;
import fr.aeris.permalink.rest.domain.Role;
import fr.aeris.permalink.rest.filter.jwt.JwtConfig;
import fr.aeris.permalink.rest.filter.jwt.JwtUtil;
import fr.aeris.permalink.rest.habilitation.Roles;

@RestController
@CrossOrigin
@RequestMapping(value = "/login/v1_0")
public class LoginService {

	@Autowired
	OrcidConfig orcidConfig;

	@Autowired
	AdminDao adminDao;

	@Autowired
	JwtConfig jwtConfig;

	private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);
	public static final String ORCID_KEY = "orcid";
	public static final String ACCESS_TOKEN_KEY = "token";

	@RequestMapping(value = "/isalive", method = RequestMethod.GET)
	public String isalive() {
		return "yes";
	}

	@RequestMapping(value = "/orcid", method = RequestMethod.GET)
	public ConnectionInformation orcid(HttpServletResponse response, @RequestParam String code,
			@RequestParam(name = "redirect_uri") String redirectUri) throws Exception {
		LOG.debug("orcid()");
		ClientConfig config = new DefaultClientConfig();
		config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		Client client = Client.create(config);

		WebResource service = client.resource(orcidConfig.getTokenUrl());

		MultivaluedMap<String, String> data = new MultivaluedMapImpl();
		data.add("client_id", orcidConfig.getClientId());
		data.add("client_secret", orcidConfig.getClientSecret());
		data.add("grant_type", "authorization_code");
		data.add("redirect_uri", redirectUri);
		data.add("code", code);

		ClientResponse clientResponse = service.accept("application/json").post(ClientResponse.class, data);
		if (clientResponse.getStatus() == 200) {
			OrcidToken orcidToken = clientResponse.getEntity(OrcidToken.class);
			String orcid = orcidToken.getOrcid();
			LOG.debug("orcid: " + orcid);
			if (orcid != null) {
				List<Role> roles = new ArrayList<>();
				if (adminDao.isAdmin(orcid)) {
					roles.add(new Role(Roles.ADMIN_ROLE));
				}
				String jwtToken = generateToken(orcidToken, getRoleNames(roles));
				ConnectionInformation information = new ConnectionInformation();
				information.setName(orcidToken.getName());
				information.setRoles(roles);
				information.setToken(jwtToken);
				information.setOrcid(orcidToken.getOrcid());
				return information;
			} else {
				LOG.warn("Orcid is null");
			}
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		} else {
			response.setStatus(clientResponse.getStatus());
			return null;
		}

	}

	private Set<String> getRoleNames(List<Role> roles) {
		Set<String> result = new HashSet<>();
		for (Role role : roles) {
			result.add(role.getName());
		}
		return result;
	}

	private String generateToken(OrcidToken orcidToken, Set<String> roles) throws Exception {
		Map<String, String> infos = new HashMap<>();
		infos.put(ORCID_KEY, orcidToken.getOrcid());

		String token = JwtUtil.generateToken(orcidToken.getName(), jwtConfig.getSigningKey(),
				jwtConfig.getTokenValidity(), roles, infos);
		return token;
	}

}
