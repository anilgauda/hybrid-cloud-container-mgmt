package ie.ncirl.container.manager.app.service;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import ie.ncirl.container.manager.app.converters.RegisterApplicationConvertor;
import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.ApplicationRepo;
import ie.ncirl.container.manager.app.repository.ContainerDeploymentRepo;
import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.app.repository.VMRepo;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.app.vo.DashboardVo;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.common.domain.enums.Role;
import ie.ncirl.container.manager.library.configurevm.VMConfigureTest;

@RunWith(SpringRunner.class)
@SpringBootTest(value = { "spring.profiles.active=test" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DashBoardServiceTest {

	@Autowired
	DashboardService dashboardService;

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

	@Before
	public void setUp() {

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

	@Test
	public void testGetDashboardDetails() {
		DashboardVo dashBoardVo=dashboardService.getDashboardDetails();
		Assert.assertNotNull(dashBoardVo);
	}

	@After
	public void clearData() {
		containerRepo.deleteAll();
    	appRepo.deleteAll();
        vmRepo.deleteAll();
        providerRepo.deleteAll();
        userRepo.deleteAll();
	}

	private VMDTO buildVMDTO(String vmName) {
		return VMDTO.builder().host(VMConfigureTest.IP_ADDRESS).name(vmName).privateKey(KeyUtils.inString(VMConfigureTest.privateKey)).username(VMConfigureTest.USERNAME).providerId(providerRepo.findAll().get(0).getId()).build();

	}

	private RegisterApplicationDto buildRegAppDto(String appName) {
		return RegisterApplicationDto.builder().name(appName).build();
	}

	private ContainerDeployment buildContainerObj(String containerId, VM vm, Application application) {
		return ContainerDeployment.builder().containerId(containerId).vm(vm).application(application).build();
	}

}
