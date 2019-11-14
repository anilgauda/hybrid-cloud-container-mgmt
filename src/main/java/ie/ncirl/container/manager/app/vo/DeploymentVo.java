package ie.ncirl.container.manager.app.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentVo {
	private String vmId;
	private String deploymentType;
}
