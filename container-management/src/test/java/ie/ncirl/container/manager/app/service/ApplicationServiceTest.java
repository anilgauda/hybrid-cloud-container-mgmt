package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.converters.RegisterApplicationConvertor;
import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.*;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.app.vo.PageApplicationVo;
import ie.ncirl.container.manager.common.domain.*;
import ie.ncirl.container.manager.common.domain.enums.Role;
import ie.ncirl.container.manager.stubs.VMConfigStub;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"spring.profiles.active=test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ApplicationServiceTest {

    @Autowired
    private ProviderRepo providerRepo;

    @Autowired
    ApplicationService applicationService;

    @Autowired
    VMService vmService;

    @Autowired
    UserRepo userRepo;

    @Autowired
    VMRepo vmRepo;

    @Autowired
    ApplicationRepo appRepo;

    @Autowired
    ApplicationService appService;

    @Autowired
    ContainerDeploymentService containerService;

    @Autowired
    ContainerDeploymentRepo containerRepo;

    @Autowired
    VMConverter vmconvertor;

    @Autowired
    RegisterApplicationConvertor appConvertor;

    Logger logger = Logger.getLogger(ApplicationServiceTest.class.getName());

    @Before
    public void setUp() throws Exception {
        User user = User.builder().username("admin").role(Role.USER).build();
        userRepo.save(user);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("admin");
        authentication.getAuthorities();
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Provider provider = Provider.builder().name("adminProvider").build();
        providerRepo.save(provider);
        vmService.save(buildVMDTO("adminVM1"));
        vmService.save(buildVMDTO("adminVM2"));
        vmService.save(buildVMDTO("adminVM3"));

        appService.saveApplication(buildRegAppDto("App1"));
        appService.saveApplication(buildRegAppDto("App2"));
        appService.saveApplication(buildRegAppDto("App3"));
        appService.saveApplication(buildRegAppDto("App4"));
        VMDTO vmDto = vmService.getAllVMs().get(0);
        VM vm = vmconvertor.from(vmDto);
        vm.setProvider(provider);
        ContainerDeployment container = buildContainerObj("test", vm, appConvertor.from(appService.getApplicationsByUser().get(0)));
        containerRepo.save(container);

    }

    private ContainerDeployment buildContainerObj(String containerId, VM vm, Application application) {

        return ContainerDeployment.builder().containerId(containerId).vm(vm).application(application).build();
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    public void testGetRunningApp() {
        PageApplicationVo applications = applicationService.getRunningApplication(1, 2);
        Assert.assertNotNull("List of applications fetched failed ", applications);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    public void testGetApplicationById() {
        RegisterApplicationDto applications = applicationService.getApplicationById(appService.getApplicationsByUser().get(0).getId());
        Assert.assertNotNull("List of applications fetched failed ", applications);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    public void testGetApplicationByName() {
        RegisterApplicationDto applications = applicationService.getApplicationByName(appService.getApplicationsByUser().get(0).getName());
        Assert.assertNotNull("List of applications fetched failed ", applications);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    public void testDeleteApplicationById() {
        Long appID = appService.getApplicationsByUser().get(0).getId();
        try {
            applicationService.deleteApplicationById(appID);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to Delete application");
        }
        RegisterApplicationDto applications = applicationService.getApplicationById(appID);
        Assert.assertNotNull(applications);
    }

    @Test
    @WithMockUser(username = "user", authorities = {"USER"})
    public void testStopApplication() {
        Long appID = appService.getApplicationsByUser().get(0).getId();
        applicationService.stopApplication(appID);
        RegisterApplicationDto applications = applicationService.getApplicationById(appID);
        Assert.assertNotNull(applications);
    }

    @After
    public void deleteData() throws Exception {
        containerRepo.deleteAll();
        appRepo.deleteAll();
        vmRepo.deleteAll();
        providerRepo.deleteAll();
        userRepo.deleteAll();

    }

    private VMDTO buildVMDTO(String vmName) {
        return VMDTO.builder()
                .host(VMConfigStub.IP_ADDRESS)
                .name(vmName)
                .privateKey(KeyUtils.inString(VMConfigStub.privateKey))
                .username(VMConfigStub.USERNAME).providerId(providerRepo.findAll().get(0).getId()).build();

    }

    private RegisterApplicationDto buildRegAppDto(String appName) {
        return RegisterApplicationDto.builder().name(appName).build();

    }
}
