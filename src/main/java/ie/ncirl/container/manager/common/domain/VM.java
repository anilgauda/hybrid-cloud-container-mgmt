package ie.ncirl.container.manager.common.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.*;

@ToString
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name = "vms")
@Table(name = "vms")
public class VM {

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
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


    private byte[] privateKey;

    private LocalDateTime lastAccess;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Provider provider;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vm")
    @Builder.Default
    private List<ContainerDeployment> containerDeployments = new ArrayList<>();
}
