package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;

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

    public AllocationData getAllocations(Application application, Integer numDeployments, List<VM> servers) {
        return strategy.getAllocationData(application, numDeployments, servers);
    }
}
