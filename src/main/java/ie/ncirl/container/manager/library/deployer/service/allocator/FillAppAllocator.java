package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.library.deployer.service.allocator.AppAllocator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FillAppAllocator implements AppAllocator {
    @Override
    public Map<String, Application> getAllocations(Application application, Integer numDeployments, List<String> serverPrivateKeys) {
        Map<String, Application> allocationMap = new HashMap<>();
        return allocationMap;
    }
}
