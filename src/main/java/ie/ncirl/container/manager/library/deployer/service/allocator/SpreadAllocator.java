package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import ie.ncirl.container.manager.library.deployer.dto.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpreadAllocator implements AppAllocatorStrategy {
    @Override
    public AllocationData getAllocationData(Application application, Integer numDeployments, List<Server> servers) {
        List<Allocation> allocations = new ArrayList<>();
        int pendingAllocations = numDeployments;
        for (Server server : servers) {

            List<Application> applications = server.getApplications();
            Optional<Application> deployment = applications.stream().filter(
                    app -> app.equals(application)
            ).findAny();

            if (!deployment.isPresent()) {
                pendingAllocations -= 1;
                allocations.add(Allocation.builder().server(server).application(application).count(1).build());
            }
            if (pendingAllocations == 0) break;
        }

        return AllocationData.builder().allocations(allocations).failedAllocations(pendingAllocations).build();
    }
}
