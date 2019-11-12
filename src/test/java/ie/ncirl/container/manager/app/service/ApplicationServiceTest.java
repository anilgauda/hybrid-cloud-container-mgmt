package ie.ncirl.container.manager.app.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import ie.ncirl.container.manager.app.dto.RunningApplicationDto;

@RunWith(SpringRunner.class)
@SpringBootTest(value = { "spring.profiles.active=dev" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ApplicationServiceTest {

	@Autowired
	ApplicationService applicationService;
	
	@Test
	public void testGetRunningApp() {
		List<RunningApplicationDto> applications=applicationService.getRunningApplication();
		Assert.assertNotNull("List of applications fetched failed ", applications);
	}
}
