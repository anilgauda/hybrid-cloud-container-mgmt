package ie.ncirl.container.manager.app.vo;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class OptimizeSelectVo {
    private List<String> vmIds;
}
