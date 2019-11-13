package ie.ncirl.container.manager.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ie.ncirl.container.manager.app.util.KeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.converters.RegisterApplicationConvertor;
import ie.ncirl.container.manager.app.dto.ContainerDto;
import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.dto.RunningApplicationDto;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.ApplicationRepo;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.User;
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
	UserUtil userUtil;

	@Autowired
	private RegisterApplicationConvertor convertor;

	public List<RunningApplicationDto> getRunningApplication() {
		List<VMDTO> listOfVms = vmService.getAllVMs();// need to get vm baised on user
		ContainerConfig config = new ContainerConfig();
		List<RunningApplicationDto> applications = new ArrayList<>();
		for (VMDTO vm : listOfVms) {
			RunningApplicationDto app = new RunningApplicationDto();
			List<ContainerDto> containers = new ArrayList<>();
			app.setProviderName(providerService.getAllProviders().get(0).getName()); // Should be Multiple Providers
			ArrayList<String> linuxContainers = new ArrayList<>();
			try {
				linuxContainers = config.getContainerIds(KeyUtils.inBytes(vm.getPrivateKey()), vm.getUsername(), vm.getHost());
			} catch (ContainerException e) {
				logger.error("Failed to Fetch Container Ids", e);
			}
			for (String containerId : linuxContainers) {
				ContainerDto container = new ContainerDto();
				Map<String, String> containerStats = new HashMap<>();
				try {
					containerStats = config.getContainerStats(KeyUtils.inBytes(vm.getPrivateKey()), vm.getUsername(), vm.getHost(), containerId);
				} catch (ContainerException e) {
					logger.error("Failed to get statistics of given container", e);
				}
				container.setContainerId(containerId);
				container.setContainerStats(containerStats);
				containers.add(container);
			}
			app.setContainers(containers);
			applications.add(app);
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
		List<Application> applications = applicationRepo.getAllApplicationByUserId(userUtil.getCurrentUser().getId());
		for (Application application : applications) {
			registeredApplcationList.add(convertor.from(application));
		}
		return registeredApplcationList;
	}

	public RegisterApplicationDto getApplicationByName(String name) {
		Application application = applicationRepo.findByName(name);
		if (application!=null) {
			return convertor.from(application);
		}
		return null;
	}
	public RegisterApplicationDto getApplicationById(Long id) {
		Application application=applicationRepo.getOne(id);
		if (application!=null) {
			return convertor.from(application);
		}
		return null;
	}
	public void deleteApplicationById(Long id) {
		applicationRepo.deleteById(id);
	}
}
