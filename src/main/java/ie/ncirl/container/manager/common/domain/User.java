package ie.ncirl.container.manager.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Id
    private Long id;

    private String username;

    private String email;

    private String password;

    private LocalDateTime created;

    private String avatar;

    //@OneToOne(fetch = FetchType.LAZY, optional = false)
    //private Role role;
}
