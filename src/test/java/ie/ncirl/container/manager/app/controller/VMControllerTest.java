package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.enums.Role;
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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
public class VMControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Before
    public void setUp() throws Exception {
        userRepo.save(User.builder().username("admin").role(Role.USER).build());
        userRepo.save(User.builder().username("test").role(Role.GUEST).build());

    }

    @After
    public void tearDown() throws Exception {
        userRepo.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = "USER")
    public void shouldReturnAllVmForAdmin() throws Exception {
        this.mockMvc.perform(get("/vms/view"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Servers")));
    }

    @Test
    @WithMockUser(username = "test", roles = "GUEST")
    public void shouldReturnEmptyForTest() throws Exception {
        this.mockMvc.perform(get("/vms/view"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Servers")))
                .andExpect(model().attribute("vms", hasSize(0)));
    }


}
