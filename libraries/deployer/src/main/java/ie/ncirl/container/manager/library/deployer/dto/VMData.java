package ie.ncirl.container.manager.library.deployer.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VMData {

    private Long id;

    /**
     * Name to easily identify the VM
     */
    private String name;

    /**
     * Can be domain or ip of the server/VM
     */
    private String host;

    /**
     * The user with which to login into the VM
     */
    private String username;

    /**
     * Available memory of VM only populated on create!
     */
    private Integer memory;

    private byte[] privateKey;

    @Builder.Default
    private List<ContainerDeploymentData> containerDeployments = new ArrayList<>();
}
