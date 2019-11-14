package ie.ncirl.container.manager.app.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.converters.RegisterApplicationConvertor;
import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.ApplicationRepo;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.app.vo.ApplicationVo;
import ie.ncirl.container.manager.app.vo.ContainerVo;
import ie.ncirl.container.manager.app.vo.VmVo;
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

	public List<ApplicationVo> getRunningApplication() {
		List<VMDTO> listOfVms = vmService.findByUserId(userUtil.getCurrentUser().getId());
		List<Application> listofApplication=applicationRepo.findAllByUserId(userUtil.getCurrentUser().getId());
		List<ApplicationVo> applicationVos=new ArrayList<>();
		
		for(Application application:listofApplication) {
			ApplicationVo applicationVo=new ApplicationVo();
			applicationVo.setApplicationName(application.getName());
			List<VmVo> vmVos=new ArrayList<>();
			for(VMDTO vms:listOfVms) {
				VmVo vm=new VmVo();
				vm.setName(vms.getName());
				ContainerConfig config = new ContainerConfig();
				ArrayList<String> linuxContainers = new ArrayList<>();
				try {
					linuxContainers = config.getContainerIds(KeyUtils.inBytes(vms.getPrivateKey()), vms.getUsername(), vms.getHost(),application.getRegistryImageUrl());// Should be with multiple application
				} catch (ContainerException e) {
					logger.error("Failed to Fetch Container Ids", e);
				}
				List<ContainerVo> containerVos=new ArrayList<>();
				for (String containerId : linuxContainers) {
					ContainerVo containers=new ContainerVo();
					containers.setContainerId(containerId);
					try {
						containers.setStats(config.getContainerStats(KeyUtils.inBytes(vms.getPrivateKey()), vms.getUsername(), vms.getHost(),containerId));
					} catch (ContainerException e) {
						logger.error("Failed to get statistics of given container", e);
					}
					containerVos.add(containers);
				}
				vm.setContainers(containerVos);
				vm.setProviderName(providerService.findById(vms.getProviderId()).getName());
				vmVos.add(vm);
			}
			applicationVo.setVms(vmVos);
			applicationVos.add(applicationVo);
		}
		return applicationVos;
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
	public void deleteContainerById(String containerId) {
		List<VMDTO> listOfVms = vmService.findByUserId(userUtil.getCurrentUser().getId());
		ContainerConfig config = new ContainerConfig();	
		//config.stopContainers(privateKey, userName, ipAddress, containerIds);
	}
}
