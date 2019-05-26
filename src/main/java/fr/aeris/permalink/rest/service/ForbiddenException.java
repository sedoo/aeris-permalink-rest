package fr.aeris.permalink.rest.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason="You don't have the right to do this operation")
public class ForbiddenException extends RuntimeException {}