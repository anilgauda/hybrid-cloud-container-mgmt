package ie.ncirl.container.manager.common.domain;

import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Cloud providers like AWS, Azure, etc
 */
@Entity
@Builder
public class Provider {

    @Id
    private Long id;

    private String name;
}
