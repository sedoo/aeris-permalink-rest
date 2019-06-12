package fr.aeris.permalink.rest.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="Incorrect parameters")
public class BadRequestException extends RuntimeException {}