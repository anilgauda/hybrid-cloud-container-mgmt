package ie.ncirl.container.manager.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import ie.ncirl.container.manager.app.vo.DeploymentVo;
import ie.ncirl.container.manager.app.vo.OptimizationVo;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.common.domain.enums.Role;
import ie.ncirl.container.manager.library.configurevm.VMConfigureTest;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;

@RunWith(SpringRunner.class)
@SpringBootTest(value = { "spring.profiles.active=test" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ContainerDeploymentServiceTest {

	@Autowired
	private ProviderRepo providerRepo;

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
	

	Logger logger = Logger.getLogger(ContainerDeploymentServiceTest.class.getName());

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

	@After
	public void deleteData() throws Exception {
		containerRepo.deleteAll();
		appRepo.deleteAll();
		vmRepo.deleteAll();
		providerRepo.deleteAll();
		userRepo.deleteAll();

	}

	@Test
	public void testGetDockerApplications() {
		List<String> containerId=new ArrayList<>();
		containerId.add("test");
		List<Application> applications=containerService.getDockerApplications(containerId);
		Assert.assertNotNull(applications);
	}
	
	@Test
	public void testGetContainersByAppId() {
		List<ContainerDeployment> containerList=containerService.getContainersByAppId(appService.getApplicationsByUser().get(0).getId());
		Assert.assertNotNull(containerList);
	}
	
	@Test
	public void testGetAllocationDataFill() {
		DeploymentVo deploymentVo=new DeploymentVo();
		List<String> vmIds=new ArrayList<>();
		vmIds.add(vmService.getAllVMs().get(0).getId().toString());
		deploymentVo.setVmIds(vmIds);
		deploymentVo.setDeploymentType("100");
		deploymentVo.setNumDeployments(1);
		VM vm=vmconvertor.from(buildVMDTO("adminVM1"));
		List<VM> vms=new ArrayList<>();
		vms.add(vm);
		AllocationData allocationData=containerService.getAllocationData(appService.getApplicationsByUser().get(0).getId().toString(), deploymentVo);
		Assert.assertNotNull(allocationData);
	}
	
	@Test
	public void testGetAllocationDataSpread() {
		DeploymentVo deploymentVo=new DeploymentVo();
		List<String> vmIds=new ArrayList<>();
		vmIds.add(vmService.getAllVMs().get(0).getId().toString());
		deploymentVo.setVmIds(vmIds);
		deploymentVo.setDeploymentType("200");
		deploymentVo.setNumDeployments(1);
		VM vm=vmconvertor.from(buildVMDTO("adminVM1"));
		List<VM> vms=new ArrayList<>();
		vms.add(vm);
		AllocationData allocationData=containerService.getAllocationData(appService.getApplicationsByUser().get(0).getId().toString(), deploymentVo);
		Assert.assertNotNull(allocationData);
	}
	
	@Test
	public void testGetOptimizationChanges() {
		VM vm=vmconvertor.from(buildVMDTO("adminVM1"));
		List<VM> vms=new ArrayList<>();
		vms.add(vm);
		vm=vmconvertor.from(buildVMDTO("adminVM2"));
		vms.add(vm);
		List<OptimizationVo> optimizationVos=containerService.getOptimizationChanges(vms);
		Assert.assertNotNull(optimizationVos);
	}
	
	@Test
	public void testOptimizeContainers() {
		VM vm=vmconvertor.from(buildVMDTO("adminVM1"));
		List<VM> vms=new ArrayList<>();
		vms.add(vm);
		vm=vmconvertor.from(buildVMDTO("adminVM2"));
		vms.add(vm);
		try {
			containerService.optimizeContainers(vms, 100, 50);
		} catch (ContainerException e) {
			logger.log(Level.SEVERE, "Unable to deploy applications");
		}
	}

	private VMDTO buildVMDTO(String vmName) {
		return VMDTO.builder().host(VMConfigureTest.IP_ADDRESS).name(vmName).privateKey(KeyUtils.inString(VMConfigureTest.privateKey)).username(VMConfigureTest.USERNAME).providerId(providerRepo.findAll().get(0).getId()).memory(100).build();

	}

	private RegisterApplicationDto buildRegAppDto(String appName) {
		return RegisterApplicationDto.builder().name(appName).memory(10).build();

	}

	private ContainerDeployment buildContainerObj(String containerId, VM vm, Application application) {

		return ContainerDeployment.builder().containerId(containerId).vm(vm).application(application).build();
	}

}