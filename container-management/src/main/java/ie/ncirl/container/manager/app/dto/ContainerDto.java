package ie.ncirl.container.manager.app.dto;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ContainerDto {
	 private String containerId;
	    private Map<String, String> containerStats;
}
