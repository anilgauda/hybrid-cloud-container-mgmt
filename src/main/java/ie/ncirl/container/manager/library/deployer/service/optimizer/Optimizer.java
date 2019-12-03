package ie.ncirl.container.manager.library.deployer.service.optimizer;

import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.deployer.dto.OptimalContainer;

import java.util.List;

public interface Optimizer {
    enum VMOrder {ASC_MEM, DESC_MEM, AS_IS}

    List<OptimalContainer> getOptimizedContainers(List<VM> vms, VMOrder vmOrder) throws ContainerException;

    void setApplicationResourceConsumption(List<VM> vms) throws ContainerException;
}
