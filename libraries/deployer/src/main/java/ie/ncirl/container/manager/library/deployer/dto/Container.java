package ie.ncirl.container.manager.library.deployer.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode
public class Container {

    private String id;

    private VMData server;
    private ApplicationData applicationData;

    private Integer memory;
    private Integer cpu;
}
