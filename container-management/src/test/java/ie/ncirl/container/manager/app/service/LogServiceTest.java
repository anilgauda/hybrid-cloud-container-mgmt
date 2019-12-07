package ie.ncirl.container.manager.app.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import ie.ncirl.container.manager.app.repository.LogsRepo;
import ie.ncirl.container.manager.common.domain.Logs;

@RunWith(SpringRunner.class)
@SpringBootTest(value = { "spring.profiles.active=test" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LogServiceTest {

	@Autowired
	LogsRepo logRepo;
	
	@Autowired
	LogsService logService;

	@Before
	public void setUp() throws Exception {
		for(int i=0;i<10;i++) {
			Logs log=Logs.builder().details("Test 1").build();
			logRepo.save(log);
		}
	}
	
	@Test
	public void testSaveLogs() {
		Logs log=Logs.builder().details("Test 1").build();
		logService.saveLogs(log);
	}
	
	@Test
	public void testGetLogs() {
		Page<Logs> logs=logService.getAllLogs(PageRequest.of(0,10));
		Assert.assertEquals(10, logs.getTotalElements());
	}
	@After
	public void cleanUp() {
		logRepo.deleteAll();
	}

}
