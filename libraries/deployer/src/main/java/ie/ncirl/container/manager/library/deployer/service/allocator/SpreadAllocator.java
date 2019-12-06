package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.library.deployer.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpreadAllocator implements AppAllocatorStrategy {
    @Override
    public AllocationData getAllocationData(ApplicationData reqApplicationData, Integer numDeployments, List<VMData> servers) {
        List<Allocation> allocations = new ArrayList<>();
        int pendingAllocations = numDeployments;
        for (VMData server : servers) {
            List<ApplicationData> applicationDataList = server.getContainerDeployments().stream()
                    .map(ContainerDeploymentData::getApplicationData).distinct()
                    .collect(Collectors.toList());

            Optional<ApplicationData> deployment = applicationDataList.stream().filter(
                    app -> app.equals(reqApplicationData)
            ).findAny();

            // Gets the allocatable container based on memory (although code is part of this strategy this is moved
            //  into the interface as this method is common across strategies. Other strategies will be built on this)
            int allocatableContainersInVM = getAllocatableContainersInVM(reqApplicationData, server);

            if (!deployment.isPresent() && allocatableContainersInVM > 0) {
                pendingAllocations -= 1;
                allocations.add(Allocation.builder().server(server).applicationData(reqApplicationData).count(1).build());
            }
            if (pendingAllocations == 0) break;
        }

        return AllocationData.builder().allocations(allocations).failedAllocations(pendingAllocations).build();
    }
}
