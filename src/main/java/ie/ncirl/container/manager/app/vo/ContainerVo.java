package ie.ncirl.container.manager.app.vo;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerVo {
	private String vmName;
	private String providerName;
	private String containerId;
	private Map<String, String> stats;
}
