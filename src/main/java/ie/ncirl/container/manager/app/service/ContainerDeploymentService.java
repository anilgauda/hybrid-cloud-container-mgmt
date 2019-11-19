package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.converters.RegisterApplicationConvertor;
import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.repository.ContainerDeploymentRepo;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.app.vo.DeploymentVo;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.common.domain.enums.DeploymentType;
import ie.ncirl.container.manager.library.configurevm.ContainerConfig;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import ie.ncirl.container.manager.library.deployer.service.allocator.AppAllocator;
import ie.ncirl.container.manager.library.deployer.service.allocator.FillAllocator;
import ie.ncirl.container.manager.library.deployer.service.allocator.SpreadAllocator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class monitors all dockers in a given VM and returns required metrics
 * like different dockers running in the VM and the amount of resources used by
 * each docker in the given VM
 */
@Service
@Slf4j
public class ContainerDeploymentService {
    @Autowired
    private
    ApplicationService applicationService;

    @Autowired
    private
    VMService vmService;

    @Autowired
    UserUtil userUtil;

    @Autowired
    private
    ContainerDeploymentRepo containerRepo;

    @Autowired
    private
    RegisterApplicationConvertor appConverter;

    @Autowired
    private VMConverter vmConverter;

    /**
     * Get the docker applications running in a VM
     *
     * @param containerIds container ids running in a VM
     * @return List of Application
     */
    public List<Application> getDockerApplications(List<String> containerIds) {
        return new ArrayList<>();
    }

    /**
     * Deploys the given application in given vm. Deploys numDeployments containers in the VM
     *
     * @param application    Application
     * @param vm             VM where docker application will be deployed
     * @param numDeployments Number of copies of this application to be deployed/started in the VM
     */
    public void deployContainers(Application application, VM vm, int numDeployments) {
        while (numDeployments-- > 0) {
            deployContainer(application, vm);
        }
    }

    private void deployContainer(Application application, VM vm) {
        log.debug(String.format("Deploying application [name:%s, uri:%s] in VM [id:%d name: %s]",
                application.getName(), application.getRegistryImageUrl(), vm.getId(), vm.getName()));
        List<String> containerIds = new ArrayList<>();
        ContainerConfig config = new ContainerConfig();
        try {
            containerIds = config.startContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(),
                    application.getRegistryImageUrl());
        } catch (ContainerException e) {
            System.out.println("Error Occurred while creating container");
            log.error("Error occurred while starting container", e);
        }
        for (String containerId : containerIds) {
            ContainerDeployment containerDeployment = ContainerDeployment.builder().containerId(containerId)
                    .application(application).vm(vm).deployedOn(LocalDateTime.now()).build();
            saveContainers(containerDeployment);
        }
    }

    private void saveContainers(ContainerDeployment containerDeployment) {
        containerRepo.save(containerDeployment);
    }

    void deleteContainersByContainerId(Long appId) {
        List<ContainerDeployment> containers = containerRepo.findAllByApplicationId(appId);
        ContainerConfig config = new ContainerConfig();
        for (ContainerDeployment container : containers) {
            VM vm = container.getVm();
            List<String> containerList = new ArrayList<>();
            containerList.add(container.getContainerId());
            try {
                config.stopContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), containerList);
            } catch (ContainerException e) {
                System.out.println("Failed to Stop containers");
            }
        }
        containerRepo.deleteByApplicationId(appId);
    }

    List<ContainerDeployment> getContainersByAppId(Long appId) {
        return containerRepo.findAllByApplicationId(appId);
    }

    /**
     * Gets Allocation data for deploying application is selected VMs with selected algorithm
     *
     * @param appId        Application id
     * @param deploymentVo DeploymentVo generated from form
     * @return Allocations
     */
    public AllocationData getAllocationData(@PathVariable("Id") String appId, @ModelAttribute @Valid DeploymentVo deploymentVo) {
        AppAllocator allocator = new AppAllocator();
        switch (DeploymentType.fromCode((Integer.parseInt(deploymentVo.getDeploymentType())))) {
            case FILL:
                allocator.setAppAllocatorStrategy(new FillAllocator());
                break;
            case SPREAD:
                allocator.setAppAllocatorStrategy(new SpreadAllocator());
                break;
        }
        RegisterApplicationDto applicationDto = applicationService.getApplicationById(Long.parseLong(appId));
        Application application = appConverter.from(applicationDto);
        List<VM> servers = deploymentVo.getVmIds().stream()
                .map(vmId -> vmService.findVMById(Long.parseLong(vmId))).collect(Collectors.toList());
        return allocator.getAllocations(application, deploymentVo.getNumDeployments(), servers);
    }

}
