package ie.ncirl.container.manager.common.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vms")
public class VM {

    @Id
    private Long id;

    /**
     * The private key file (pem file) which will be used to ssh into the server
     */
    private String keyFileName;

    /**
     * The user with which to login into the VM
     */
    private String username;

    /**
     * Can be domain or ip of the server/VM
     */
    private String host;

    /**
     * This must be filled while creating the VM. This is the available memory when no
     * allocations or deployments have been made.
     */
    private Integer memory; // In GB

    private LocalDateTime lastAccess;
    
    @Lob
    private byte[] privateKey;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Provider provider;

    @OneToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<ContainerDeployment> containerDeployments = new ArrayList<>();
}
