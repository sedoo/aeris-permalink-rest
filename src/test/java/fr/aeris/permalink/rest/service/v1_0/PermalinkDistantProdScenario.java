package fr.aeris.permalink.rest.service.v1_0;

import java.util.List;

//import org.junit.FixMethodOrder;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import fr.aeris.permalink.rest.config.Profiles;
import fr.aeris.permalink.rest.dao.PermalinkDao;
import fr.aeris.permalink.rest.dao.PermalinkRepository;
import fr.aeris.permalink.rest.domain.Permalink;
import fr.aeris.permalink.rest.domain.Statistics;


//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT) 
//@AutoConfigureMockMvc
//@ActiveProfiles(Profiles.DISTANT_PRODUCTION_PROFILE)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PermalinkDistantProdScenario {

	@Autowired
	PermalinkRepository repository;
	
	@Autowired
	PermalinkDao dao;
	

//	@Test
//	public void testStatistiques() throws Exception {
//		Statistics statistics = dao.getStatistics();
//		List<Permalink> findAllByOrcid = dao.findAllByOrcid("0000-0001-7778-7401");
//		System.out.println("coucou");
//	}
	

}
