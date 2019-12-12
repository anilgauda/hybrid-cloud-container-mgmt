package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.converters.*;
import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.repository.ContainerDeploymentRepo;
import ie.ncirl.container.manager.app.util.CryptUtil;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.app.vo.DeploymentVo;
import ie.ncirl.container.manager.app.vo.OptimizationVo;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.Logs;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.common.domain.enums.AppDeployStrategy;
import ie.ncirl.container.manager.common.domain.enums.DeploymentType;
import ie.ncirl.container.manager.common.domain.logging.ContainerLogs;
import ie.ncirl.container.manager.common.domain.logging.Log;
import ie.ncirl.container.manager.library.configurevm.ContainerConfig;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.configurevm.model.Container;
import ie.ncirl.container.manager.library.configurevm.model.ContainersList;
import ie.ncirl.container.manager.library.configurevm.strategy.ApplicationWeightedStrategy;
import ie.ncirl.container.manager.library.configurevm.strategy.DeploymentStrategy;
import ie.ncirl.container.manager.library.configurevm.strategy.VMWeightedStrategy;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import ie.ncirl.container.manager.library.deployer.dto.ApplicationData;
import ie.ncirl.container.manager.library.deployer.dto.OptimalContainer;
import ie.ncirl.container.manager.library.deployer.dto.VMData;
import ie.ncirl.container.manager.library.deployer.service.allocator.AppAllocator;
import ie.ncirl.container.manager.library.deployer.service.allocator.FillAllocator;
import ie.ncirl.container.manager.library.deployer.service.allocator.SpreadAllocator;
import ie.ncirl.container.manager.library.deployer.service.optimizer.Optimizer;
import ie.ncirl.container.manager.library.deployer.service.optimizer.ZigZagOptimizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
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

    @Autowired
    private CryptUtil cryptUtil;

    @Autowired
    private DeployerLibAdapter deployerLibAdapter;

    @Autowired
    private LogsService logservice;

    @Autowired
    private UserUtil userUtil;

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
     * @param applicationData ApplicationData
     * @param vmData          VMData where docker applicationData will be deployed
     * @param numDeployments  Number of copies of this applicationData to be deployed/started in the VM
     * @throws ContainerException
     */
    public void deployContainers(ApplicationData applicationData, VMData vmData, int numDeployments) throws ContainerException {
        while (numDeployments-- > 0) {
            deployContainer(applicationData, vmData);
        }
    }

    private void deployContainer(ApplicationData applicationData, VMData vmData) throws ContainerException {
        log.debug(String.format("Deploying applicationData [name:%s, uri:%s] in VM [id:%d name: %s]",
                applicationData.getName(), applicationData.getRegistryImageUrl(), vmData.getId(), vmData.getName()));
        List<String> containerIds = new ArrayList<>();
        ContainerConfig config = new ContainerConfig();
        try {
            containerIds = config.startContainers(vmData.getPrivateKey(), vmData.getUsername(), vmData.getHost(),
                    applicationData.getRegistryImageUrl());
        } catch (ContainerException e) {
            log.error("Error occurred while starting container", e);
        }
        if (containerIds.size() > 0) {
            for (String containerId : containerIds) {
                RegisterApplicationDto applicationDto = applicationService.getApplicationById(applicationData.getId());
                Application application = appConverter.from(applicationDto);
                VM vm = vmService.findVMById(vmData.getId());
                ContainerDeployment containerDeployment = ContainerDeployment.builder().containerId(containerId)
                        .application(application).vm(vm).deployedOn(LocalDateTime.now()).build();
                saveContainers(containerDeployment);
            }
        } else {
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
        createLog(containerRepo.findByContainerId(containerId), "Delete");
    }


    private void saveContainers(ContainerDeployment containerDeployment) {
        containerRepo.save(containerDeployment);
        createLog(containerDeployment, "Create");
    }

    void deleteContainersByContainerId(Long appId) {
        List<ContainerDeployment> containers = containerRepo.findAllByApplicationId(appId);
        ContainerConfig config = new ContainerConfig();
        for (ContainerDeployment container : containers) {
            VM vm = container.getVm();
            List<String> containerList = new ArrayList<>();
            containerList.add(container.getContainerId());
            try {
                config.stopContainers(KeyUtils.inBytes(cryptUtil.decryptBytes(vm.getPrivateKey()))
                        , vm.getUsername(), vm.getHost(), containerList);
            } catch (ContainerException e) {
                log.debug("Failed to Stop containers");
            }
            createLog(containerRepo.findByContainerId(container.getContainerId()), "Delete");

        }
        containerRepo.deleteByApplicationId(appId);

    }

    public List<ContainerDeployment> getContainersByAppId(Long appId) {
        return containerRepo.findAllByApplicationId(appId);
    }

    /**
     * Gets Allocation data for deploying applicationData is selected VMs with selected algorithm
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
        List<VMData> servers = deploymentVo.getVmIds().stream()
                .map(vmId -> deployerLibAdapter.fromVM(vmService.findVMById(Long.parseLong(vmId))))
                .collect(Collectors.toList());
        return allocator.getAllocations(deployerLibAdapter.fromApplication(application), deploymentVo.getNumDeployments(), servers);
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
            optimalContainers = optimizer.getOptimizedContainers(deployerLibAdapter.fromVMs(vms), Optimizer.VMOrder.AS_IS);
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
            optimizer.setApplicationResourceConsumption(deployerLibAdapter.fromVMs(vms));
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
            OptimizationVo vm = optimizationMap.get(optimalContainer.getOptimalVMData().getName());
            vm.setCpuAfter(vm.getCpuAfter() + optimalContainer.getContainer().getCpu());
            vm.setMemoryAfter(vm.getMemoryAfter() + optimalContainer.getContainer().getMemory());
        }

        return optimizationMap.values().stream()
                .sorted(Comparator
                        .comparing(OptimizationVo::getMemoryAfter, Comparator.reverseOrder())
                        .thenComparing(OptimizationVo::getCpuAfter, Comparator.reverseOrder())
                )
                .collect(Collectors.toList());
    }

    /**
     * Optimizes all VMs by moving them optimally across VMs
     *
     * @param vms          VMs where optimization algorithm will run
     * @param strategyCode Type of strategy
     * @param weight       Weight
     * @throws ContainerException
     */
    public void optimizeContainers(List<VM> vms, int strategyCode, int weight) throws ContainerException {
        List<OptimalContainer> optimalContainers = getOptimalContainers(vms);
        DeploymentStrategy strategy = new DeploymentStrategy();

        switch (AppDeployStrategy.fromCode(strategyCode)) {
            case ApplicationWeighted:
                strategy.setStrategy(new ApplicationWeightedStrategy());
                break;
            case VMWeighted:
                strategy.setStrategy(new VMWeightedStrategy());
                break;
        }
        ContainersList containerLists = strategy.execute(optimalConvertor.fromOptimalContainers(optimalContainers), weight);
        List<Container> deployedContainers = containerLists.getDeployedContainers();
        List<Container> unDeployedContainers = containerLists.getUndeployedContainers();
        log.debug("Containers to be Deployed" + deployedContainers.toString());
        log.debug("Containers to be Undeployed" + unDeployedContainers.toString());
        for (Container container : deployedContainers) {
            deployContainer(container.getId(), modelAppConvertor.from(container.getApplication()), modelVMConvertor.from(container.getServer()));

        }
        for (Container container : unDeployedContainers) {
            undeployContainer(container.getId(), modelVMConvertor.from(container.getServer()));

        }
    }

    private void deployContainer(String containerID, Application application, VM vm) {
        ContainerDeployment containerDeployment = ContainerDeployment.builder().containerId(containerID)
                .application(application).vm(vm).deployedOn(LocalDateTime.now()).build();
        saveContainers(containerDeployment);

    }

    public void createLog(ContainerDeployment container, String operation) {
        Log appLog = new ContainerLogs();
        Logs log = Logs.builder().details(appLog.createLogData(container, operation, userUtil.getCurrentUser().getUsername(), userUtil.getCurrentUserRole())).build();
        logservice.saveLogs(log);
    }
}
