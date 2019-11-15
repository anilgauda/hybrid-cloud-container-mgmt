package ie.ncirl.container.manager.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.converters.RegisterApplicationConvertor;
import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.ContainerDeploymentRepo;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.ContainerConfig;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;

/**
 * This class monitors all dockers in a given VM and returns required metrics
 * like different dockers running in the VM and the amount of resources used by
 * each docker in the given VM
 */
@Service
public class ContainerDeploymentService {
	@Autowired
	ApplicationService applicationService;

	@Autowired
	VMService vmService;

	@Autowired
	UserUtil userUtil;

	@Autowired
	ContainerDeploymentRepo containerRepo;

	@Autowired
	RegisterApplicationConvertor appConvertor;

	@Autowired
	VMConverter vmConvertor;
	
	/**
	 * Get the docker applications running in a VM
	 * 
	 * @param containerIds container ids running in a VM
	 * @return List of Application
	 */
	public List<Application> getDockerApplications(List<String> containerIds) {
		return new ArrayList<>();
	}

	public void deployContainers(Long applicationId, Long vmId, Integer deploymentType) {
		RegisterApplicationDto application = applicationService.getApplicationById(applicationId);
		List<String> containerIds = new ArrayList<>();
		VMDTO vmDto = vmService.findById(vmId);
		ContainerConfig config = new ContainerConfig();
		try {
			containerIds = config.startContainers(KeyUtils.inBytes(vmDto.getPrivateKey()), vmDto.getUsername(), vmDto.getHost(), application.getRegistryImageUrl());
		} catch (ContainerException e) {
			System.out.println("Error Occured while creating container");
		}
		for (String containerId : containerIds) {
			ContainerDeployment containerDeployment = ContainerDeployment.builder().containerId(containerId).application(appConvertor.from(application)).vm(vmConvertor.from(vmDto)).deployedOn(LocalDateTime.now()).build();
			saveContainers(containerDeployment);
		}
	}

	public void saveContainers(ContainerDeployment containerDeployment) {
		containerRepo.save(containerDeployment);
	}
	public void deleteContainersByContainerId(Long appId) {
		List<ContainerDeployment> containers=containerRepo.findAllByApplicationId(appId);
		ContainerConfig config = new ContainerConfig();
		for(ContainerDeployment container: containers) {
			VM vm=container.getVm();
			List<String> containerList=new ArrayList<>();
			containerList.add(container.getContainerId());
			try {
				config.stopContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), containerList);
			} catch (ContainerException e) {
				System.out.println("Failed to Stop containers");
			}
		}
		containerRepo.deleteByApplicationId(appId);
	}
	public List<ContainerDeployment> getContainersByAppId(Long appId){
		return containerRepo.findAllByApplicationId(appId);
	}
}
