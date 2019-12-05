package ie.ncirl.container.manager.common.domain;

import ie.ncirl.container.manager.common.domain.listener.VMPostInsertListener;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity(name = "vms")
@EntityListeners(VMPostInsertListener.class)
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

    /**
     * Available memory of VM only populated on create!
     */
    private Integer memory;

    private byte[] privateKey;

    private LocalDateTime lastAccess;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Provider provider;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vm")
    @Builder.Default
    @ToString.Exclude
    private List<ContainerDeployment> containerDeployments = new ArrayList<>();
}
