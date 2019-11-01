package ie.ncirl.container.manager.library.deployer.dto;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.VM;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

/**
 * Contains the allocations for a given strategy
 */
@Builder
@Getter
@EqualsAndHashCode
public class AllocationData {

    private List<Allocation> allocations;

    /**
     * If there was no space for allocation, this number will identify the number of
     * applications/deployments that could not be allocated
     */
    @Builder.Default
    private int failedAllocations = 0;
}
