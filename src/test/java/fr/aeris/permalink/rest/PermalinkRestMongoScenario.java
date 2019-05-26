package fr.aeris.permalink.rest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.aeris.permalink.rest.config.Profiles;
import fr.aeris.permalink.rest.dao.PermalinkDao;
import fr.aeris.permalink.rest.dao.PermalinkRepository;
import fr.aeris.permalink.rest.domain.Permalink;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT) 
@AutoConfigureMockMvc
@ActiveProfiles(Profiles.MONGO_LOCAL_PROFILE)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PermalinkRestMongoScenario {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	PermalinkRepository repository;
	
	@Autowired
	PermalinkDao dao;
	
	@Value("classpath:francois.andre.jwt")
	Resource adminJwtFile;
	
	@Value("classpath:damien.boulanger.jwt")
	Resource managerJwtFile;
	
	private String damienBoulangerOrcid = "0000-0001-6935-1106";

	@Test
	public void testIsAlive() throws Exception {
		mvc.perform(get("/admin/isalive")).andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(containsString("yes")));
	}
	
	
	@Test
	public void listPermalinkWithoutAuthorisation() throws Exception {
		mvc.perform(get("/admin/list")).andDo(print()).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
	}
	
	@Test
	public void listPermalinkAsAdmin() throws Exception {
		String token = IOUtils.toString(adminJwtFile.getInputStream(), Charset.defaultCharset());
		MvcResult result  = mvc.perform(get("/admin/list").header("Authorization", token)).andDo(print()).andExpect(status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		Assert.assertEquals("Il doit y avoir 4 éléments", 4, countPermalinks(content));
	}
	
	@Test
	public void listPermalinkAsManager() throws Exception {
		String token = IOUtils.toString(managerJwtFile.getInputStream(), Charset.defaultCharset());
		MvcResult result  = mvc.perform(get("/admin/list").header("Authorization", token)).andDo(print()).andExpect(status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		Assert.assertEquals("Il doit y avoir 1 éléments", 1, countPermalinks(content));
	}
	
	@Test
	public void addThenDeletePermalink() throws Exception {
		
		Permalink newPermalink = new Permalink();
		newPermalink.setUrl("www.amazon.fr");
		newPermalink.setSuffix("amaZon");
		String managerToken = IOUtils.toString(managerJwtFile.getInputStream(), Charset.defaultCharset());
		MvcResult result  = mvc.perform(get("/admin/list").header("Authorization", managerToken)).andDo(print()).andExpect(status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		Assert.assertEquals("The response must contain 1 element", 1, countPermalinks(content));
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(newPermalink);
		
		String token = IOUtils.toString(managerJwtFile.getInputStream(), Charset.defaultCharset());
		mvc.perform(post("/admin/add").contentType(MediaType.APPLICATION_JSON_UTF8)
		        .content(json).header("Authorization", token)).andDo(print()).andExpect(status().isOk()).andReturn();
		
		result  = mvc.perform(get("/admin/list").header("Authorization", managerToken)).andDo(print()).andExpect(status().isOk()).andReturn();
		content = result.getResponse().getContentAsString();
		Assert.assertEquals("The response must contain 2 elements", 2, countPermalinks(content));
		
		mvc.perform(get("/Amazon")).andExpect(status().is(HttpStatus.FOUND.value())).andReturn();
		
		mvc.perform(delete("/admin/delete/amazoN").header("Authorization", managerToken)).andExpect(status().isOk()).andReturn();
		
		result  = mvc.perform(get("/admin/list").header("Authorization", managerToken)).andDo(print()).andExpect(status().isOk()).andReturn();
		content = result.getResponse().getContentAsString();
		Assert.assertEquals("The response must contain 1 element", 1, countPermalinks(content));
		
	}
	
	@Test
	public void xaddAndRemoveManager() throws Exception {
		String adminToken = IOUtils.toString(adminJwtFile.getInputStream(), Charset.defaultCharset());
		String managerToken = IOUtils.toString(managerJwtFile.getInputStream(), Charset.defaultCharset());
		MvcResult result  = mvc.perform(get("/admin/list").header("Authorization", managerToken)).andDo(print()).andExpect(status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		Assert.assertEquals("The response must contain 1 element", 1, countPermalinks(content));
		mvc.perform(get("/admin/addmanager/monde/"+damienBoulangerOrcid).header("Authorization", adminToken)).andDo(print()).andExpect(status().isOk()).andReturn();
		result  = mvc.perform(get("/admin/list").header("Authorization", managerToken)).andDo(print()).andExpect(status().isOk()).andReturn();
		content = result.getResponse().getContentAsString();		
		Assert.assertEquals("The response must contain 2 elements", 2, countPermalinks(content));
		mvc.perform(get("/admin/deletemanager/monde/"+damienBoulangerOrcid).header("Authorization", managerToken)).andDo(print()).andExpect(status().isOk()).andReturn();
		result  = mvc.perform(get("/admin/list").header("Authorization", managerToken)).andDo(print()).andExpect(status().isOk()).andReturn();
		content = result.getResponse().getContentAsString();		
		Assert.assertEquals("The response must contain 1 element", 1, countPermalinks(content));
	}
	
	
	
	@Test
	public void zDeleteUnExistingPermalink() throws Exception {
		String token = IOUtils.toString(adminJwtFile.getInputStream(), Charset.defaultCharset());
		mvc.perform(delete("/admin/delete/yoyoyo").header("Authorization", token)).andDo(print()).andExpect(status().is(HttpStatus.BAD_REQUEST.value())).andReturn();
	}
	
	@Test
	public void testRedirection() throws Exception {
		mvc.perform(get("/iagos")).andExpect(status().is(HttpStatus.FOUND.value())).andReturn();
		mvc.perform(get("/iAgOs")).andExpect(status().is(HttpStatus.FOUND.value())).andReturn();
		mvc.perform(get("/wrongurl")).andExpect(status().is(HttpStatus.NOT_FOUND.value())).andReturn();
	}
	
	@Test
	public void zDeletePermalinkAsManager() throws Exception {
		String token = IOUtils.toString(adminJwtFile.getInputStream(), Charset.defaultCharset());
		
		MvcResult result  = mvc.perform(get("/admin/list").header("Authorization", token)).andDo(print()).andExpect(status().isOk()).andReturn();
		String content = result.getResponse().getContentAsString();
		int initialCount = countPermalinks(content);
		mvc.perform(delete("/admin/delete/monde").header("Authorization", token)).andDo(print()).andExpect(status().isOk());
		result  = mvc.perform(get("/admin/list").header("Authorization", token)).andDo(print()).andExpect(status().isOk()).andReturn();
		content = result.getResponse().getContentAsString();
		int finalCount = countPermalinks(content);
		Assert.assertEquals(initialCount-1, finalCount);
	}

	private int countPermalinks(String content) {
		return StringUtils.countMatches(content, "suffix");
	}

	@Before
	public final void init() {
		repository.deleteAll();
		dao.save(create("google", "http://www.google.com","1234"));
		dao.save(create("monde", "http://www.lemonde.fr","12"));
		dao.save(create("equipe", "http://www.lequipe.fr","12"));
		dao.save(create("iagos", "http://www.iagos-data.fr/","0000-0001-6935-1106"));
	}
	
	private Permalink create(String suffix, String url, String... orcids ) {
		Permalink permalink = new Permalink();
		permalink.setSuffix(suffix);
		permalink.setUrl(url);
		ArrayList<String> managers = new ArrayList<>();
		for (String orcid : orcids) {
			managers.add(orcid);
		}
		permalink.setManagerIds(managers);
		return permalink;
	}

}
