package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import ie.ncirl.container.manager.library.deployer.dto.ApplicationData;
import ie.ncirl.container.manager.library.deployer.dto.VMData;

import java.util.ArrayList;
import java.util.List;

public class FillAllocator implements AppAllocatorStrategy {
    @Override
    public AllocationData getAllocationData(ApplicationData applicationData, Integer numDeployments, List<VMData> vms) {
        List<Allocation> allocations = new ArrayList<>();
        int pendingAllocations = numDeployments;
        int allocatableContainersInVM, allocatedContainersInVM;
        for (VMData vmData : vms) {
            allocatableContainersInVM = getAllocatableContainersInVM(applicationData, vmData);
            if (allocatableContainersInVM > 0) {
                allocatedContainersInVM = Math.min(allocatableContainersInVM, pendingAllocations);
                pendingAllocations -= allocatedContainersInVM;
                allocations.add(Allocation.builder().server(vmData).applicationData(applicationData).count(allocatedContainersInVM).build());
            }
            if (pendingAllocations == 0) break;
        }

        return AllocationData.builder().allocations(allocations).failedAllocations(pendingAllocations).build();
    }
}
