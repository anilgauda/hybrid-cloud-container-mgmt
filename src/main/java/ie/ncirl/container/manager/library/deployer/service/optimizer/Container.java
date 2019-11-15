package ie.ncirl.container.manager.library.deployer.service.optimizer;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.library.deployer.dto.Server;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Container {

    private String id;

    private Server server;
    private Application application;

    private Integer memory;
    private Integer cpu;
}
