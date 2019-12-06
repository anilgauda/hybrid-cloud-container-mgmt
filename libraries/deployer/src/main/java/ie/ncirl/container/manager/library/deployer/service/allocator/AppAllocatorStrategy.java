package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import ie.ncirl.container.manager.library.deployer.dto.ApplicationData;
import ie.ncirl.container.manager.library.deployer.dto.ContainerDeploymentData;
import ie.ncirl.container.manager.library.deployer.dto.VMData;

import java.util.List;

/**
 * Strategy -> Spread => Same type of docker avoid
 * Strategy -> Optimize => Optimize memory & cpu
 * Strategy -> Fill => FIFO based on memory
 */

public interface AppAllocatorStrategy {

    /**
     * It will return the allocations for each docker applicationData based on strategy
     *
     * @param applicationData    The docker app
     * @param numDeployments How many dockers must be deployed ?
     * @param vms            he decrypted server private keys where these images can be deployed into
     * @return allocation data
     */
    AllocationData getAllocationData(ApplicationData applicationData, Integer numDeployments, List<VMData> vms);


    /**
     * Gets number of containers of an applicationData that can be allocated in a VMData based on memory
     *
     * @param applicationData ApplicationData Container
     * @param vmData          VMData where container is being deployed
     * @return no. of deployments that can be made
     */
    default int getAllocatableContainersInVM(ApplicationData applicationData, VMData vmData) {
        int allocatableContainersInVM;
        List<ContainerDeploymentData> deployments = vmData.getContainerDeployments();

        // Same containers will query db every time to get applicationData. TODO: Improvement possible
        Integer usedMemory = deployments.stream().mapToInt(
                deployment -> deployment.getApplicationData().getMemory()
        ).sum();

        // How many containers/deployments of this applications can be allocated in this VMData ?
        Integer availableMemory = vmData.getMemory() - usedMemory;

        allocatableContainersInVM = (int) (availableMemory / applicationData.getMemory());
        return allocatableContainersInVM;
    }

}
