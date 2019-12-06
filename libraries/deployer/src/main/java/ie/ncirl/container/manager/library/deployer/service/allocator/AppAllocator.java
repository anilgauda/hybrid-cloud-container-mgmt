package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import ie.ncirl.container.manager.library.deployer.dto.ApplicationData;
import ie.ncirl.container.manager.library.deployer.dto.VMData;

import java.util.List;

/**
 * Follows a strategy design pattern to define and execute the allocation algorithms
 * that implement the interface AppAllocatorStrategy
 * Ref: https://refactoring.guru/design-patterns/strategy
 */
public class AppAllocator {

    private AppAllocatorStrategy strategy;

    public void setAppAllocatorStrategy(AppAllocatorStrategy strategy) {
        this.strategy = strategy;
    }

    public AllocationData getAllocations(ApplicationData applicationData, Integer numDeployments, List<VMData> servers) {
        return strategy.getAllocationData(applicationData, numDeployments, servers);
    }
}
