package ie.ncirl.container.manager.app.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentVo {
    private List<String> vmIds;
    private String deploymentType;

    @Min(value = 1, message = "Count should be atleast 1")
    private Integer numDeployments;

    private String allocation; // Only used to return error for failed allocation
}
