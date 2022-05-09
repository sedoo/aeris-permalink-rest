package fr.aeris.permalink.rest.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;


@OpenAPIDefinition(servers = { @Server(url = "${springdoc.server.url}")})
public class OpenApi3Config {
}

