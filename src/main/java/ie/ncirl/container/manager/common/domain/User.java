package ie.ncirl.container.manager.common.domain;

import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
public class User {

    @Id
    private Long id;

    private String username;

    private String email;

    private String password;

    private LocalDateTime created;

    private String avatar;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Role role;
}
