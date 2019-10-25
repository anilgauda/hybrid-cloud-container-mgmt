package ie.ncirl.container.manager.library.deployer.service.allocator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class monitors all dockers in a given VM and returns required metrics like different dockers
 * running in the VM and the amount of resources used by each docker in the given VM
 */
public class DockerMetricManager {

    /**
     * Get the docker applications running in a VM
     *  1. Get the container ids from a VM
     *  2. Get the docker applications mapped to the container id in the database
     * @param privateKey private key for VM
     * @return
     */
    public List<String> getDockerApplications(String privateKey) {

        return new ArrayList<>();
    }
}
