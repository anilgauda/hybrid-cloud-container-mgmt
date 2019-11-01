package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.common.domain.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * This class monitors all dockers in a given VM and returns required metrics like different dockers
 * running in the VM and the amount of resources used by each docker in the given VM
 */
public class ContainerDeploymentService {

    /**
     * Get the docker applications running in a VM
     * @param containerIds container ids running in a VM
     * @return List of Application
     */
    public List<Application> getDockerApplications(List<String> containerIds) {


        return new ArrayList<>();
    }
}
