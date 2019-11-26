package ie.ncirl.container.manager.library.deployer.dto;

import ie.ncirl.container.manager.app.dto.DTO;
import ie.ncirl.container.manager.common.domain.VM;
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
public class OptimalContainer implements DTO{

    private Container container;

    private VM optimalVM;
}
