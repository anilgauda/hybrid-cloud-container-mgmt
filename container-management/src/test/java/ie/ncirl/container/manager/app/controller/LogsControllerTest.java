package ie.ncirl.container.manager.app.controller;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import ie.ncirl.container.manager.app.repository.LogsRepo;
import ie.ncirl.container.manager.common.domain.Logs;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = "USER",authorities="USER")
public class LogsControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private LogsRepo logsRepo;

	@Before
	public void setUp() throws Exception {
		for (int i = 0; i < 10; i++) {
			Logs log = Logs.builder().details("Test 1").build();
			logsRepo.save(log);
		}
	}
	
	@Test
	public void testGetLogs() throws Exception {
		 this.mockMvc.perform(get("/logs/view"))
         .andDo(print())
         .andExpect(status().isOk())
         .andExpect(content().string(containsString("Logs")));
	}

	@After
	public void cleanUp() {
		logsRepo.deleteAll();
	}

}
