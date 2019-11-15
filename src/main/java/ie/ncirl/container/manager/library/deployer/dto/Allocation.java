package ie.ncirl.container.manager.library.deployer.dto;

import ie.ncirl.container.manager.common.domain.Application;
import lombok.Builder;
import lombok.EqualsAndHashCode;

/**
 * This is a simple class used to define an allocation for a particular VM
 * It defines in a VM which application and how many instances/deployments
 * of that application must be allocated
 */
@Builder
@EqualsAndHashCode
public class Allocation {

    private Application application;

    private Server server;

    /**
     * By default there will be at least one application allocated
     */
    @Builder.Default
    private int count = 0;
}
