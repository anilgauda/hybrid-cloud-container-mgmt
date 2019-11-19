package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpreadAllocator implements AppAllocatorStrategy {
    @Override
    public AllocationData getAllocationData(Application application, Integer numDeployments, List<VM> servers) {
        List<Allocation> allocations = new ArrayList<>();
        int pendingAllocations = numDeployments;
        for (VM server : servers) {
            List<Application> applications = server.getContainerDeployments().stream()
                    .map(ContainerDeployment::getApplication).distinct()
                    .collect(Collectors.toList());
            Optional<Application> deployment = applications.stream().filter(
                    app -> app.equals(application)
            ).findAny();

            int allocatableContainersInVM = getAllocatableContainersInVM(application, server);
            if (!deployment.isPresent() && allocatableContainersInVM > 0) {
                pendingAllocations -= 1;
                allocations.add(Allocation.builder().server(server).application(application).count(1).build());
            }
            if (pendingAllocations == 0) break;
        }

        return AllocationData.builder().allocations(allocations).failedAllocations(pendingAllocations).build();
    }
}
