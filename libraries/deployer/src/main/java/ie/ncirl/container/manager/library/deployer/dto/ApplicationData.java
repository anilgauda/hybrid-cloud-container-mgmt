package ie.ncirl.container.manager.library.deployer.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationData {

    private Long id;

    private String name;

    private String registryImageUrl;

    @Builder.Default
    private Integer cpu = 0; // In % of total VMData CPU predicted to be used.. This is used to optimize allocation

    @Builder.Default
    private Integer memory = 0; // In MB of memory that app will use.. This is used in allocators
}
