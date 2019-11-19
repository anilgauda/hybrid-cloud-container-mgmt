package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;

import java.util.ArrayList;
import java.util.List;

public class FillAllocator implements AppAllocatorStrategy {
    @Override
    public AllocationData getAllocationData(Application application, Integer numDeployments, List<VM> vms) {
        List<Allocation> allocations = new ArrayList<>();
        int pendingAllocations = numDeployments;
        int allocatableContainersInVM, allocatedContainersInVM;
        for (VM vm : vms) {
            allocatableContainersInVM = getAllocatableContainersInVM(application, vm);
            if (allocatableContainersInVM > 0) {
                allocatedContainersInVM = Math.min(allocatableContainersInVM, pendingAllocations);
                pendingAllocations -= allocatedContainersInVM;
                allocations.add(Allocation.builder().server(vm).application(application).count(allocatedContainersInVM).build());
            }
            if (pendingAllocations == 0) break;
        }

        return AllocationData.builder().allocations(allocations).failedAllocations(pendingAllocations).build();
    }
}
