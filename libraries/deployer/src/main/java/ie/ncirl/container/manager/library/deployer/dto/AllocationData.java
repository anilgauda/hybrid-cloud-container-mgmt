package ie.ncirl.container.manager.library.deployer.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * Contains the allocations for a given strategy
 */
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class AllocationData {

    private List<Allocation> allocations;

    /**
     * If there was no space for allocation, this number will identify the number of
     * applications/deployments that could not be allocated
     */
    @Builder.Default
    private int failedAllocations = 0;
}
