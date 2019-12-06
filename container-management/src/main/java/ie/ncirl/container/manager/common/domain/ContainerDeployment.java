package ie.ncirl.container.manager.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@ToString
@Entity(name = "container_deployments")
@Table(name = "container_deployments")
@NoArgsConstructor
@AllArgsConstructor
public class ContainerDeployment {

	@GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    /**
     * Unique Docker container id deployed in a VM
     */
    private String containerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private VM vm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Application application;

    private LocalDateTime deployedOn;
}
