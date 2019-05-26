package fr.aeris.permalink.rest.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Configuration
public class WebConfiguration extends WebMvcConfigurerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(WebConfiguration.class);

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		LOG.debug("configureMessageConverters()");

		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();//.modules(m);
		converters.add(new MappingJackson2HttpMessageConverter(builder.serializationInclusion(Include.ALWAYS).build()));
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		.allowedMethods( "GET", "PUT", "POST", "DELETE", "PATCH");
	}
}