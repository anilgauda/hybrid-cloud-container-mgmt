package ie.ncirl.container.manager.common.domain;

import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
public class ContainerDeployment {

    @Id
    private Long id;

    /**
     * Unique Docker container id deployed in a VM
     */
    private String containerId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private VM vm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Application application;

    private LocalDateTime deployedOn;
}
