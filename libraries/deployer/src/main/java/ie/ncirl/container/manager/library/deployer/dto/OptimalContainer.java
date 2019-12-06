package ie.ncirl.container.manager.library.deployer.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The result of optimal allocations from optimization algorithm
 * Which container will go in which VMData ?
 */
@Builder
@Data
@EqualsAndHashCode
public class OptimalContainer {

    private Container container;

    private VMData optimalVMData;
}
