package fr.aeris.permalink.rest.service.v1_0.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason="Service offer limit reached")
public class ServiceOfferLimitReachedException extends RuntimeException {}