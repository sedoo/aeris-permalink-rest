package fr.aeris.permalink.rest.service.v1_0.exception;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.aeris.permalink.rest.filter.jwt.JwtUtil;

@RestController
@CrossOrigin
@RequestMapping(value = "/token/v1_0")
public class TokenService {

	@RequestMapping(value = "/isalive", method = RequestMethod.GET)
	public String isalive() {
		return "yes";
	}

	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public String refreshToken(@RequestHeader("Authorization") String authHeader, HttpServletResponse response) {
		return StringUtils.trimToEmpty(response.getHeader(JwtUtil.AUTH_HEADER));
	}

}
