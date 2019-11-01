package ie.ncirl.container.manager.app.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@ToString
@Getter
@Setter
public class Application {
    private String containerId;
    private Map<String, String> containerStats;
}
