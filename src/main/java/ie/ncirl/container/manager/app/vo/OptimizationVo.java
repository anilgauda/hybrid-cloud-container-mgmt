package ie.ncirl.container.manager.app.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OptimizationVo {
    private String vmName;
    private Integer memoryBefore;
    private Integer cpuBefore;

    @Builder.Default
    private Integer memoryAfter = 0;

    @Builder.Default
    private Integer cpuAfter = 0;
}
