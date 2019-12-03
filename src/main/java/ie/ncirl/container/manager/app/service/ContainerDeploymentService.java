package ie.ncirl.container.manager.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ie.ncirl.container.manager.library.deployer.service.optimizer.OptimizerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.converters.ModelAppConvertor;
import ie.ncirl.container.manager.app.converters.ModelVMConvertor;
import ie.ncirl.container.manager.app.converters.OptimalContainerConvertor;
import ie.ncirl.container.manager.app.converters.RegisterApplicationConvertor;
import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.repository.ContainerDeploymentRepo;
import ie.ncirl.container.manager.app.vo.DeploymentVo;
import ie.ncirl.container.manager.app.vo.OptimizationVo;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.common.domain.enums.AppDeployStrategy;
import ie.ncirl.container.manager.common.domain.enums.DeploymentType;
import ie.ncirl.container.manager.library.configurevm.ContainerConfig;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.configurevm.model.Container;
import ie.ncirl.container.manager.library.configurevm.model.ContainersList;
import ie.ncirl.container.manager.library.configurevm.strategy.ApplicationWeightedStrategy;
import ie.ncirl.container.manager.library.configurevm.strategy.DeploymentStrategy;
import ie.ncirl.container.manager.library.configurevm.strategy.VMWeightedStrategy;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import ie.ncirl.container.manager.library.deployer.dto.OptimalContainer;
import ie.ncirl.container.manager.library.deployer.service.allocator.AppAllocator;
import ie.ncirl.container.manager.library.deployer.service.allocator.FillAllocator;
import ie.ncirl.container.manager.library.deployer.service.allocator.SpreadAllocator;
import ie.ncirl.container.manager.library.deployer.service.optimizer.Optimizer;
import ie.ncirl.container.manager.library.deployer.service.optimizer.ZigZagOptimizer;
import lombok.extern.slf4j.Slf4j;

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
    private
    ContainerDeploymentRepo containerRepo;

    @Autowired
    private
    RegisterApplicationConvertor appConverter;

    @Autowired
    private VMConverter vmConverter;
    
    @Autowired
    private OptimalContainerConvertor optimalConvertor;
    
    @Autowired
    private ModelAppConvertor modelAppConvertor;
    
    @Autowired
    private ModelVMConvertor modelVMConvertor;

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
     * @throws ContainerException 
     */
    public void deployContainers(Application application, VM vm, int numDeployments) throws ContainerException {
        while (numDeployments-- > 0) {
            deployContainer(application, vm);
        }
    }

    private void deployContainer(Application application, VM vm) throws ContainerException {
        log.debug(String.format("Deploying application [name:%s, uri:%s] in VM [id:%d name: %s]",
                application.getName(), application.getRegistryImageUrl(), vm.getId(), vm.getName()));
        List<String> containerIds = new ArrayList<>();
        ContainerConfig config = new ContainerConfig();
        try {
            containerIds = config.startContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(),
                    application.getRegistryImageUrl());
        } catch (ContainerException e) {
            log.error("Error occurred while starting container", e);
        }
        if(containerIds.size()>0) {
        for (String containerId : containerIds) {
            ContainerDeployment containerDeployment = ContainerDeployment.builder().containerId(containerId)
                    .application(application).vm(vm).deployedOn(LocalDateTime.now()).build();
            saveContainers(containerDeployment);
        }
        }else {
        	throw new ContainerException("Public Repository name is incorrect", null);
        }
    }

    /**
     * Stops docker container in a VM and removes the ContainerDeployment object from db
     *
     * @param containerId Container id
     * @param vm          VM
     */
    private void undeployContainer(String containerId, VM vm) {
        List<String> containerIds = new ArrayList<>();
        containerIds.add(containerId);      
        containerRepo.deleteByContainerId(containerId);
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

    public List<ContainerDeployment> getContainersByAppId(Long appId) {
        return containerRepo.findAllByApplicationId(appId);
    }

    /**
     * Gets Allocation data for deploying application is selected VMs with selected algorithm
     *
     * @param appId        Application id
     * @param deploymentVo DeploymentVo generated from form
     * @return Allocations
     */
    public AllocationData getAllocationData(String appId, DeploymentVo deploymentVo) {
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

    /**
     * Optimizes all contains in vms by running the ZigZag optimizer algorithm
     *
     * @param vms VMs
     * @return Optimal Containers
     */
    private List<OptimalContainer> getOptimalContainers(List<VM> vms) {
        Optimizer optimizer = new ZigZagOptimizer();
        List<OptimalContainer> optimalContainers = new ArrayList<>();
        try {
            optimalContainers = optimizer.getOptimizedContainers(vms, Optimizer.VMOrder.AS_IS);
        } catch (ContainerException e) {
            log.error("Failed to optimize vm containers", e);
        }

        return optimalContainers;
    }


    /**
     * Gets before/after container changes in VMs
     *
     * @param vms VMs where optimization algorithm will run
     */
    public List<OptimizationVo> getOptimizationChanges(List<VM> vms) {
        List<OptimalContainer> optimalContainers = getOptimalContainers(vms);
        Map<String, OptimizationVo> optimizationMap = new HashMap<>();
        Optimizer optimizer = new ZigZagOptimizer();
        try {
            optimizer.setApplicationResourceConsumption(vms);
        } catch (ContainerException e) {
            log.error("Failed to allocate resources in vm containers", e);
        }
        for (VM vm : vms) {
            int totalMemory = 0;
            int totalCpu = 0;
            for (ContainerDeployment containerDeployment : vm.getContainerDeployments()) {
                totalMemory += containerDeployment.getApplication().getMemory();
                totalCpu += containerDeployment.getApplication().getCpu();
            }

            optimizationMap.put(vm.getName(), OptimizationVo.builder()
                    .vmName(vm.getName())
                    .memoryBefore(totalMemory)
                    .cpuBefore(totalCpu)
                    .build());
        }

        for (OptimalContainer optimalContainer : optimalContainers) {
            OptimizationVo vm = optimizationMap.get(optimalContainer.getOptimalVM().getName());
            vm.setCpuAfter(vm.getCpuAfter() + optimalContainer.getContainer().getCpu());
            vm.setMemoryAfter(vm.getMemoryAfter() + optimalContainer.getContainer().getMemory());
        }

        return new ArrayList<>(optimizationMap.values());
    }

    /**
     * Optimizes all VMs by moving them optimally across VMs
     *
     * @param vms VMs where optimization algorithm will run
     * @param strategyCode Type of strategy
     * @param weight Weight
     * @throws ContainerException 
     */
    public void optimizeContainers(List<VM> vms, int strategyCode, int weight) throws ContainerException {
        List<OptimalContainer> optimalContainers = getOptimalContainers(vms);
        DeploymentStrategy strategy=new DeploymentStrategy();
        
        switch (AppDeployStrategy.fromCode(strategyCode)) {
        case ApplicationWeighted:
        	strategy.setStrategy(new ApplicationWeightedStrategy());
            break;
        case VMWeighted:
        	strategy.setStrategy(new VMWeightedStrategy());
            break;
    }
        ContainersList containerLists= strategy.execute(optimalConvertor.fromDTOList(optimalContainers), weight);
        List<Container> deployedContainers=containerLists.getDeployedContainers();
        List<Container> unDeployedContainers=containerLists.getUndeployedContainers();
        System.out.println("Containers to be Deployed"+deployedContainers.toString());
        System.out.println("Containers to be Undeployed"+unDeployedContainers.toString());
        for(Container container: deployedContainers) {
            deployContainer(container.getId(),modelAppConvertor.from(container.getApplication()),modelVMConvertor.from( container.getServer()));

        }
        for(Container container: unDeployedContainers) {
        	undeployContainer(container.getId(), modelVMConvertor.from(container.getServer()));

        }
    }

	private void deployContainer(String containerID, Application application, VM vm) {
		ContainerDeployment containerDeployment = ContainerDeployment.builder().containerId(containerID)
                .application(application).vm(vm).deployedOn(LocalDateTime.now()).build();
        saveContainers(containerDeployment);
		
	}
}
