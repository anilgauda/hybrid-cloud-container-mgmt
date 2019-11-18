package ie.ncirl.container.manager.app.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentVo {
	private List<String> vmId;
	private String deploymentType;
}
