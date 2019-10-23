package ie.ncirl.container.manager.common.domain;

import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Builder
public class Role {

    @Id
    private Long id;

    private String name;
}
