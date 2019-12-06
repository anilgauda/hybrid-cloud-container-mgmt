package ie.ncirl.container.manager.library.deployer.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * This is a simple class used to define an allocation for a particular VMData
 * It defines in a VMData which applicationData and how many instances/deployments
 * of that applicationData must be allocated
 */
@Builder
@EqualsAndHashCode
@Getter
@ToString
public class Allocation {

    private ApplicationData applicationData;

    private VMData server;

    /**
     * By default there will be at least one applicationData allocated
     */
    @Builder.Default
    private int count = 0;
}
