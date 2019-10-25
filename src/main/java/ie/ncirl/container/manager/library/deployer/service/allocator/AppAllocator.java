package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;

import java.util.List;
import java.util.Map;

/**
 * Strategy -> Non-Sticky => Same type of docker avoid
 * Strategy -> Optimize => Optimize memory & cpu
 * Strategy -> Fill => FIFO
 */

public interface AppAllocator {

    /**
     * It will return the allocations for each docker application based on strategy
     * @param application The docker app
     * @param numDeployments How many dockers must be deployed ?
     * @param serverPrivateKeys he decrypted server private keys where these images can be deployed into
     */
    public Map<String, Application> getAllocations(Application application, Integer numDeployments, List<String> serverPrivateKeys);
}
