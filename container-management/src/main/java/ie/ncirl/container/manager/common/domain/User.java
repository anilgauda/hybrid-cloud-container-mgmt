package ie.ncirl.container.manager.common.domain;

import ie.ncirl.container.manager.common.domain.enums.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
@Table(name = "users")
public class User {

    /**
     * A default admin user created on install
     */
    public static final String ROOT_USERNAME = "root";

    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    private String username;

    private String email;

    private String password;

    private LocalDateTime created;

    private String avatar;

    @Builder.Default
    @Column
    Role role = Role.GUEST;
}
