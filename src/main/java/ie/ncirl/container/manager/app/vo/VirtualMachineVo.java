package ie.ncirl.container.manager.app.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VirtualMachineVo {
private String vmName;
private String memUtil;
private String cpuUtil;
private Integer numOfContainers;
}
