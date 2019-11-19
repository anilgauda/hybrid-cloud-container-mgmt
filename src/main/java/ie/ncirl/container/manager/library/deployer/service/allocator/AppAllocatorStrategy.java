package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;

import java.util.List;

/**
 * Strategy -> Spread => Same type of docker avoid
 * Strategy -> Optimize => Optimize memory & cpu
 * Strategy -> Fill => FIFO based on memory
 */

public interface AppAllocatorStrategy {

    /**
     * It will return the allocations for each docker application based on strategy
     *
     * @param application    The docker app
     * @param numDeployments How many dockers must be deployed ?
     * @param vms            he decrypted server private keys where these images can be deployed into
     * @return allocation data
     */
    AllocationData getAllocationData(Application application, Integer numDeployments, List<VM> vms);


    /**
     * Gets number of containers of an application that can be allocated in a VM based on memory
     * @param application Application Container
     * @param vm VM where container is being deployed
     * @return no. of deployments that can be made
     */
    default int getAllocatableContainersInVM(Application application, VM vm) {
        int allocatableContainersInVM;
        List<ContainerDeployment> deployments = vm.getContainerDeployments();
        // Same containers will query db every time to get application. TODO: Improvement possible
        Integer usedMemory = deployments.stream().mapToInt(deployment -> deployment.getApplication().getMemory()).sum();
        // How many containers/deployments of this applications can be allocated in this VM ?
        Integer availableMemory = vm.getMemory() - usedMemory;
        allocatableContainersInVM = (int) (availableMemory / application.getMemory());
        return allocatableContainersInVM;
    }

}
