package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.app.service.ProviderService;
import ie.ncirl.container.manager.common.domain.Provider;
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

import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@WithMockUser(username = "admin", roles = "USER")
public class ProviderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProviderRepo providerRepo;

    @Autowired
    private ProviderService providerService;

    @Before
    public void setUp() throws Exception {
        userRepo.save(User.builder().username("admin").role(Role.USER).build());
        userRepo.save(User.builder().username("test").role(Role.GUEST).build());
        providerRepo.save(Provider.builder().name("adminProvider").build());
        providerRepo.save(Provider.builder().name("otherProvider").build());

    }

    @After
    public void tearDown() throws Exception {
        providerRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    public void showProvidersListPage() throws Exception {
        this.mockMvc.perform(get("/providers/view"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Providers")));
    }

    @Test
    public void showCreateProviderPage() throws Exception {
        this.mockMvc.perform(get("/provider/create"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("New Provider")));
    }

    @Test
    public void showEditProviderPage() throws Exception {
        Provider provider = providerService.findByName("adminProvider");
        this.mockMvc.perform(get(String.format("/provider/%d/edit", provider.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Editing Provider")));
    }

    @Test
    public void createProviderValid() throws Exception {
        Provider provider = Provider.builder().name("prov1").build();
        this.mockMvc.perform(post("/provider")
                .flashAttr("provider", provider)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", containsString("created successfully")));
    }

    @Test
    public void createProviderInvalid() throws Exception {
        Provider provider = Provider.builder().name("adminProvider").build();
        this.mockMvc.perform(post("/provider")
                .flashAttr("provider", provider)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("provider"));
    }

    @Test
    public void editProviderValid() throws Exception {
        Provider provider = providerService.findByName("adminProvider");
        this.mockMvc.perform(post("/provider/edit")
                .flashAttr("provider", provider)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", containsString("edited successfully")));
    }

    @Test
    public void editProviderInvalid() throws Exception {
        Provider provider = providerService.findByName("adminProvider");
        provider.setName("otherProvider");
        this.mockMvc.perform(post("/provider/edit")
                .flashAttr("provider", provider)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("provider"));
    }

    @Test
    public void deleteProvider() throws Exception {
        Provider provider = providerService.findByName("adminProvider");
        this.mockMvc.perform(post(String.format("/provider/%d/delete", provider.getId()))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }
}