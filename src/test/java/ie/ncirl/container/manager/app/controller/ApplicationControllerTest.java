package ie.ncirl.container.manager.app.controller;

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.repository.ApplicationRepo;
import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.enums.Role;
@RunWith(SpringRunner.class)
@SpringBootTest(value = { "spring.profiles.active=test" })
@AutoConfigureMockMvc
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ApplicationRepo appRepo;
    
    @Autowired
    private UserRepo userRepo;
    
    @Before
	public void setUp() throws Exception {
		User user=User.builder().username("admin").role(Role.USER).build();
		userRepo.save(user);
		appRepo.save(Application.builder().name("app1").user(user).build());
    }

    @Test
    @WithMockUser(username = "admin", roles = "USER")
    public void shouldReturnRunningApplicationsPage() throws Exception {
        this.mockMvc.perform(get("/regapp").with(user("admin")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Running Application")));
     
    }
    
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    public void testSubmitApplication() throws Exception {
    	RegisterApplicationDto applicationDto=RegisterApplicationDto.builder().name("test").build();
    	this.mockMvc.perform(
    			post("/regapp").with(user("admin")).with(csrf()).flashAttr("applicationDto",applicationDto))
    			.andDo(print())
    			.andExpect(status().is3xxRedirection());
    }
    
    
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    public void testGetRunningApplications() throws Exception {
        this.mockMvc.perform(get("/runapp").with(user("admin")).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
     
    }
    
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    public void testEditApplication() throws Exception {
    	long appId=appRepo.findAll().get(0).getId();
        this.mockMvc.perform(post("/editSave").with(user("admin")).with(csrf()).flashAttr("RegisterApplicationDto", RegisterApplicationDto.builder().Id(appId).name("app2").build()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
     
    }
    
    
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    public void testGetApplicationList() throws Exception {
        this.mockMvc.perform(get("/applicationList").with(user("admin")).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
     
    }
    
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    public void testEditProvider() throws Exception {
    	long appId=appRepo.findAll().get(0).getId();
        this.mockMvc.perform(get(String.format("/application/%d/edit", appId)).with(user("admin")).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
     
    }
    
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    public void testStopApplication() throws Exception {
    	long appId=appRepo.findAll().get(0).getId();
        this.mockMvc.perform(get(String.format("/application/%d/stop", appId)).with(user("admin")).with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
     
    }
    
    @Test
    @WithMockUser(username = "admin", roles = "USER")
    public void testDeleteApplication() throws Exception {
    	long appId=appRepo.findAll().get(0).getId();
        this.mockMvc.perform(post(String.format("/application/%d/delete", appId)).with(user("admin")).with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
     
    }
    @After
    public void cleanUp() {
    	appRepo.deleteAll();
    	userRepo.deleteAll();
    }
}