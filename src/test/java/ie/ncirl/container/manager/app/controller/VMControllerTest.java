package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.app.repository.VMRepo;
import ie.ncirl.container.manager.app.service.VMService;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.enums.Role;
import ie.ncirl.container.manager.library.configurevm.VMConfigureTest;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@WithMockUser(username = "test", roles = "GUEST")
public class VMControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProviderRepo providerRepo;

    @Autowired
    private VMRepo vmRepo;

    @Autowired
    private VMService vmService;


    @Before
    public void setUp() throws Exception {
        userRepo.save(User.builder().username("admin").role(Role.USER).build());
        userRepo.save(User.builder().username("test").role(Role.GUEST).build());
        providerRepo.save(Provider.builder().name("adminProvider").build());
        vmService.save(buildVMDTO("adminVM1"));
        vmService.save(buildVMDTO("adminVM2"));
    }

    @After
    public void tearDown() throws Exception {
        vmRepo.deleteAll();
        providerRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    public void shouldReturnAllVmForAdmin() throws Exception {
        this.mockMvc.perform(get("/vms/view"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Servers")));
    }

    @Test
    public void shouldReturnEmptyForTest() throws Exception {
        this.mockMvc.perform(get("/vms/view"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Servers")))
                .andExpect(model().attribute("vms", hasSize(2)));
    }

    @Test
    public void showCreatePage() throws Exception {
        this.mockMvc.perform(get("/vm/create"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("New")))
                .andExpect(model().attribute("availableProviders", hasSize(1)));
    }

    @Test
    public void showEditPage() throws Exception {
        VMDTO vm = vmService.findByName("adminVM1");
        this.mockMvc.perform(get(String.format("/vm/%d/edit", vm.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Edit")))
                .andExpect(model().attribute("availableProviders", hasSize(1)));
    }

    @Test
    public void createValidVM() throws Exception {
        VMDTO vmDto = buildVMDTO("vm");
        this.mockMvc.perform(post("/vm")
                .flashAttr("vm", vmDto)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", containsString("created successfully")));
    }

    @Test
    public void createInvalidVM() throws Exception {
        VMDTO vmDto = buildVMDTO("adminVM1");
        this.mockMvc.perform(post("/vm")
                .flashAttr("vm", vmDto)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("vm"));
    }

    @Test
    public void edit() throws Exception {
        VMDTO vmdto = vmService.findByName("adminVM1");
        this.mockMvc.perform(post("/vm/edit")
                .flashAttr("vmDto", vmdto)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", containsString("edited successfully")));
    }

    @Test
    public void editInvalidVM() throws Exception {
        VMDTO vmDto = vmService.findByName("adminVM1");
        vmDto.setName("adminVM2");
        this.mockMvc.perform(post("/vm/edit")
                .flashAttr("vmDto", vmDto)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("vmDto"));
    }

    @Test
    public void delete() throws Exception {
        VMDTO vmDto = vmService.findByName("adminVM1");
        this.mockMvc.perform(post(String.format("/vm/%d/delete", vmDto.getId()))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    /**
     * A common method to build a test vm object
     *
     * @param vmName Name of VM
     * @return VMDTO object
     */
    private VMDTO buildVMDTO(String vmName) {
        return VMDTO.builder().host(VMConfigureTest.IP_ADDRESS)
                .name(vmName)
                .privateKey(KeyUtils.inString(VMConfigureTest.privateKey))
                .username(VMConfigureTest.USERNAME)
                .providerId(providerRepo.findAll().get(0).getId())
                .build();

    }
}
