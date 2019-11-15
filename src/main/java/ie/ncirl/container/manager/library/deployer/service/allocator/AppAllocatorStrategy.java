package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import ie.ncirl.container.manager.library.deployer.dto.Server;

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
    AllocationData getAllocationData(Application application, Integer numDeployments, List<Server> vms);
}
