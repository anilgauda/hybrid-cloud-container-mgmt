package ie.ncirl.container.manager.common.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;


@Entity
@Builder
@Getter
@Setter
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


    private LocalDateTime lastAccess;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Provider provider;
}
