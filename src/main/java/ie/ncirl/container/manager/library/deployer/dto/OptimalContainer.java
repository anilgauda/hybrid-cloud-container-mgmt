package ie.ncirl.container.manager.library.deployer.dto;

import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.deployer.service.optimizer.Container;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The result of optimal allocations from optimization algorithm
 * Which container will go in which VM ?
 */
@Builder
@Data
@EqualsAndHashCode
public class OptimalContainer {

    private Container container;

    private VM optimalVM;
}
