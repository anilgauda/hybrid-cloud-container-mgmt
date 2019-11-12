package ie.ncirl.container.manager.app.dto;

import java.util.List;
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
public class RunningApplicationDto implements DTO{
	private static final long serialVersionUID = 1L;
	private String providerName;
    private String containerId;
    private Map<String, String> containerStats;
    List<ContainerDto> containers;
}
