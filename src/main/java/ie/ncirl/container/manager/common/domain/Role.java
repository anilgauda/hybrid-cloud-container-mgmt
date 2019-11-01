package ie.ncirl.container.manager.common.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Builder
@Getter
public class Role {

    @Id
    private Long id;

    private String name;
}
