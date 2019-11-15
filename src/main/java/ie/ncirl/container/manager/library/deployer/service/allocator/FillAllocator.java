package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import ie.ncirl.container.manager.library.deployer.dto.Server;

import java.util.ArrayList;
import java.util.List;

public class FillAllocator implements AppAllocatorStrategy {
    @Override
    public AllocationData getAllocationData(Application application, Integer numDeployments, List<Server> servers) {
        List<Allocation> allocations = new ArrayList<>();
        int pendingAllocations = numDeployments;
        int allocatableContainersInVM, allocatedContainersInVM;
        for (Server server : servers) {
            //List<ContainerDeployment> deployments = server.getContainerDeployments();
            // Same containers will query db every time to get application. TODO: Improvement possible
            //Integer usedMemory = deployments.stream().mapToInt(deployment -> deployment.getApplication().getMemory()).sum();

            // How many containers/deployments of this applications can be allocated in this VM ?
            Integer availableMemory = server.getAvailableMemory();//(server.getMemory() - usedMemory);
            allocatableContainersInVM = (int) (availableMemory / application.getMemory());
            if (allocatableContainersInVM > 0) {
                allocatedContainersInVM = Math.min(allocatableContainersInVM, pendingAllocations);
                pendingAllocations -= allocatedContainersInVM;
                allocations.add(Allocation.builder().server(server).application(application).count(allocatedContainersInVM).build());
            }
            if (pendingAllocations == 0) break;
        }

        return AllocationData.builder().allocations(allocations).failedAllocations(pendingAllocations).build();
    }
}
