package ie.ncirl.container.manager.library.deployer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContainerDeploymentData {

    private Long id;

    private String containerId;

    private ApplicationData applicationData;
}
