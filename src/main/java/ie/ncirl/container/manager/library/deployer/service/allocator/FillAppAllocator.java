package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;

import java.util.ArrayList;
import java.util.List;

public class FillAppAllocator implements AppAllocator {
    @Override
    public List<Allocation> getAllocations(Application application, Integer numDeployments, List<VM> vms) {
        List<Allocation> allocations = new ArrayList<>();

        for(VM vm: vms) {
           // List<Application> applications =
        }
        return allocations;
    }
}
