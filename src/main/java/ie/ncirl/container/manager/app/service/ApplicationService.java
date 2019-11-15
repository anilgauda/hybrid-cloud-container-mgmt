package ie.ncirl.container.manager.app.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.converters.RegisterApplicationConvertor;
import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.repository.ApplicationRepo;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.app.vo.ApplicationVo;
import ie.ncirl.container.manager.app.vo.ContainerVo;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.ContainerConfig;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApplicationService {

	Logger logger = LoggerFactory.getLogger(ApplicationService.class);

	@Autowired
	private VMService vmService;

	@Autowired
	private ProviderService providerService;

	@Autowired
	private ApplicationRepo applicationRepo;

	@Autowired
	private ContainerDeploymentService containerService;

	@Autowired
	UserUtil userUtil;

	@Autowired
	private RegisterApplicationConvertor convertor;

	public List<ApplicationVo> getRunningApplication() {
		ContainerConfig config = new ContainerConfig();
		List<ApplicationVo> applications = new ArrayList<>();
		List<Application> listofApplication = applicationRepo.findAllByUserId(userUtil.getCurrentUser().getId());
		for (Application application : listofApplication) {
			List<ContainerDeployment> containers = containerService.getContainersByAppId(application.getId());
			if (containers.size() > 0) {
				ApplicationVo applicationVo = new ApplicationVo();
				List<ContainerVo> containersVo = new ArrayList<>();
				applicationVo.setAppId(application.getId());
				applicationVo.setApplicationName(application.getName());
				for (ContainerDeployment container : containers) {
					ContainerVo containerVo = new ContainerVo();
					VM vm = container.getVm();
					Provider provider = vm.getProvider();
					containerVo.setContainerId(container.getContainerId());
					containerVo.setVmName(vm.getName());
					containerVo.setProviderName(provider.getName());
					try {
						containerVo.setStats(config.getContainerStats(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), container.getContainerId()));
					} catch (ContainerException e) {
						System.out.println("Error Occured While fetching container stats");
					}
					containersVo.add(containerVo);
				}
				applicationVo.setContainers(containersVo);
				applications.add(applicationVo);
			}

		}
		return applications;
	}

	public void saveApplication(RegisterApplicationDto regApplication) {
		Application application = convertor.from(regApplication);
		User user = userUtil.getCurrentUser();
		application.setUser(user);
		System.out.println(application.toString());
		applicationRepo.save(application);
	}

	public List<RegisterApplicationDto> getApplicationsByUser() {
		List<RegisterApplicationDto> registeredApplcationList = new ArrayList<>();
		List<Application> applications = applicationRepo.findAllByUserId(userUtil.getCurrentUser().getId());
		for (Application application : applications) {
			registeredApplcationList.add(convertor.from(application));
		}
		return registeredApplcationList;
	}

	public RegisterApplicationDto getApplicationByName(String name) {
		Application application = applicationRepo.findByName(name);
		if (application != null) {
			return convertor.from(application);
		}
		return null;
	}

	public RegisterApplicationDto getApplicationById(Long id) {
		Application application = applicationRepo.getOne(id);
		if (application != null) {
			return convertor.from(application);
		}
		return null;
	}

	public void deleteApplicationById(Long id) {
		applicationRepo.deleteById(id);
	}

	public void stopApplication(Long Id) {
		containerService.deleteContainersByContainerId(Id);
	}
}
