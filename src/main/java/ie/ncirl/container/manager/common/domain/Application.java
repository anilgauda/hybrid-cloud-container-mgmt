package ie.ncirl.container.manager.common.domain;

import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Builder
public class Application {

    @Id
    private Long id;

    private String name;

    private String registryImageUrl;

    private Integer cpuMax;

    private Integer memMax;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
}
