package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;

import java.util.ArrayList;
import java.util.List;

public class FillAppAllocator implements AppAllocatorStrategy {
    @Override
    public AllocationData getAllocationData(Application application, Integer numDeployments, List<VMDTO> vms) {
        List<Allocation> allocations = new ArrayList<>();
        int pendingAllocations = numDeployments;
        int allocatableContainersInVM, allocatedContainersInVM;
        for (VMDTO vm : vms) {
            //List<ContainerDeployment> deployments = vm.getContainerDeployments();
            // Same containers will query db every time to get application. TODO: Improvement possible
            //Integer usedMemory = deployments.stream().mapToInt(deployment -> deployment.getApplication().getMemory()).sum();

            // How many containers/deployments of this applications can be allocated in this VM ?
            Integer availableMemory = vm.getAvailableMemory();//(vm.getMemory() - usedMemory);
            allocatableContainersInVM =  (int) (availableMemory / application.getMemory());
            if (allocatableContainersInVM > 0) {
                allocatedContainersInVM = Math.min(allocatableContainersInVM, pendingAllocations);
                pendingAllocations -= allocatedContainersInVM;
                allocations.add(Allocation.builder().vm(vm).application(application).count(allocatedContainersInVM).build());
            }
            if (pendingAllocations == 0) break;
        }

        return AllocationData.builder().allocations(allocations).failedAllocations(pendingAllocations).build();
    }
}
