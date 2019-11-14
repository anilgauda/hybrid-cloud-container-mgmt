package ie.ncirl.container.manager.app.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.library.configurevm.ContainerConfig;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;

/**
 * This class monitors all dockers in a given VM and returns required metrics like different dockers
 * running in the VM and the amount of resources used by each docker in the given VM
 */
@Service
public class ContainerDeploymentService {

    /**
     * Get the docker applications running in a VM
     * @param containerIds container ids running in a VM
     * @return List of Application
     */
	@Autowired
	ApplicationService applicationService;
	
	@Autowired
	VMService vmService;
	
	@Autowired
	UserUtil userUtil;
	
    public List<Application> getDockerApplications(List<String> containerIds) {


        return new ArrayList<>();
    }
    
    public void deployContainers(Long applicationId,Long vmId,Integer deploymentType) {
    	RegisterApplicationDto application=applicationService.getApplicationById(applicationId);
    	VMDTO vmDto=vmService.findById(vmId);
    	ContainerConfig config=new ContainerConfig();
    	try {
			config.startContainers(KeyUtils.inBytes(vmDto.getPrivateKey()), vmDto.getUsername(), vmDto.getHost(), application.getRegistryImageUrl());
		} catch (ContainerException e) {
			System.out.println("Error Occured while creating container");
		}
    }
}
