package ie.ncirl.container.manager.app.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.converters.RegisterApplicationConvertor;
import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.exception.ApplicationException;
import ie.ncirl.container.manager.app.repository.ApplicationRepo;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.app.vo.ApplicationVo;
import ie.ncirl.container.manager.app.vo.ContainerVo;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.common.domain.enums.Role;
import ie.ncirl.container.manager.library.configurevm.ContainerConfig;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApplicationService {

	/** The logger. */
	Logger logger = LoggerFactory.getLogger(ApplicationService.class);

	/** The vm service. */
	@Autowired
	private VMService vmService;

	/** The provider service. */
	@Autowired
	private ProviderService providerService;

	/** The application repo. */
	@Autowired
	private ApplicationRepo applicationRepo;

	/** The container service. */
	@Autowired
	private ContainerDeploymentService containerService;

	/** The user utility */
	@Autowired
	UserUtil userUtil;

	/** The convertor. */
	@Autowired
	private RegisterApplicationConvertor convertor;

	/**
	 * Gets the all the running applications from application tables and fetch there
	 * stats from running virtual machine.
	 *
	 * @return the running application
	 */
	public List<ApplicationVo> getRunningApplication() {
		ContainerConfig config = new ContainerConfig();
		List<ApplicationVo> applications = new ArrayList<>();
		Pageable firstFiveElements = PageRequest.of(0, 2);
		/** Get all the running container from application repo **/
		Page<Application> listofApplication;
		if (userUtil.getCurrentUserRole().contains(Role.USER.name())) {
			listofApplication = applicationRepo.findAll(firstFiveElements);
		} else {
			listofApplication = applicationRepo.findAllByUserId(userUtil.getCurrentUser().getId(), firstFiveElements);

		}
		for (Application application : listofApplication) {
			List<ContainerDeployment> containers = containerService.getContainersByAppId(application.getId());
			if (containers.size() > 0) {
				/** Create applicationvo to display it on page **/
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
						logger.error("Error Occured While fetching container stats");
					}
					containersVo.add(containerVo);
				}
				applicationVo.setContainers(containersVo);
				applications.add(applicationVo);
			}

		}
		return applications;
	}

	/**
	 * Save application.
	 *
	 * @param regApplication the registered application
	 */
	public void saveApplication(RegisterApplicationDto regApplication) {
		Application application = convertor.from(regApplication);
		User user = userUtil.getCurrentUser();
		application.setUser(user);
		System.out.println(application.toString());
		applicationRepo.save(application);
	}

	/**
	 * Gets the applications by user.
	 *
	 * @return the applications by user
	 */
	public List<RegisterApplicationDto> getApplicationsByUser() {
		List<Application> applications;
		if (userUtil.getCurrentUserRole().contains(Role.USER.name())) {
			applications = applicationRepo.findAll();
		} else {
			applications = applicationRepo.findAllByUserId(userUtil.getCurrentUser().getId());

		}
		return convertor.fromDomainList(applications);
	}

	/**
	 * Gets the application by name.
	 *
	 * @param name the name
	 * @return the application by name
	 */
	public RegisterApplicationDto getApplicationByName(String name) {
		Application application = applicationRepo.findByName(name);
		if (application != null) {
			return convertor.from(application);
		}
		return null;
	}

	/**
	 * Gets the application by id.
	 *
	 * @param id the id
	 * @return the application by id
	 */
	public RegisterApplicationDto getApplicationById(Long id) {
		Application application = applicationRepo.getOne(id);
		if (application != null) {
			return convertor.from(application);
		}
		return null;
	}

	/**
	 * Delete application by id.
	 *
	 * @param id the id
	 * @throws ApplicationException
	 */
	public void deleteApplicationById(Long id) throws ApplicationException {
		List<ContainerDeployment> runningContainers = containerService.getContainersByAppId(id);
		if (runningContainers.size() > 0) {
			applicationRepo.deleteById(id);
		} else {
			throw new ApplicationException("Unable to Delete Application Please delete the stop the containers");
		}
	}

	/**
	 * Stop application.
	 *
	 * @param Id the id
	 */
	public void stopApplication(Long Id) {
		containerService.deleteContainersByContainerId(Id);
	}


}
