package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.dto.UserDTO;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = "USER",authorities="USER")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Before
    public void setUp() throws Exception {
        User user = User.builder().username("admin").role(Role.USER).build();
        User guest = User.builder().username("guest").role(Role.GUEST).build();
        userRepo.save(user);
        userRepo.save(guest);
    }

    @After
    public void cleanUp() {
        userRepo.deleteAll();
    }

    @Test
    public void registration() throws Exception {
        this.mockMvc.perform(get("/user/register").with(user("admin")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Register")));
    }

    @Test
    public void showUserList() throws Exception {
        this.mockMvc.perform(get("/user/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("List")))
                .andExpect(model().attribute("users", hasSize(2)));
    }

    @Test
    public void createValidUser() throws Exception {
        UserDTO userDTO = UserDTO.builder().username("test").password("pass").confirmPassword("pass").build();
        this.mockMvc.perform(post("/user/register")
                .flashAttr("user", userDTO)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void returnErrorForInValidUser() throws Exception {
        UserDTO userDTO = UserDTO.builder().username("test").password("pass").confirmPassword("passDiff").build();
        this.mockMvc.perform(post("/user/register")
                .flashAttr("user", userDTO)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("user"));
    }

    @Test
    public void modifyRole() throws Exception {
        User user = userRepo.findByUsername("admin");
        this.mockMvc.perform(post(String.format("/user/%d/role/%s", user.getId(), Role.GUEST.name()))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "guest", roles = "GUEST")
    public void shouldNotmodifyRole() throws Exception {
        User user = userRepo.findByUsername("admin");
        this.mockMvc.perform(post(String.format("/user/%d/role/%s", user.getId(), Role.GUEST.name()))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void showLoginPage() throws Exception {
        this.mockMvc.perform(get("/user/login").with(user("admin")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login")));
    }
}