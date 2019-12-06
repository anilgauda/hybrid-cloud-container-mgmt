package ie.ncirl.container.manager.app.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DashboardVo {
private int totalAppNo;
private int totalVmNo;
private int totalConNo;
private int providerNo;
private List<VirtualMachineVo> vmList;
}
