package ie.ncirl.container.manager.library.deployer.service.optimizer;

import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;

import java.util.List;

public interface Optimizer {
    public AllocationData getAllocationData(List<VM> vms) throws ContainerException, DockerException;
}
