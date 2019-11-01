package ie.ncirl.container.manager.common.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Builder
@Getter
public class Application {

    @Id
    private Long id;

    private String name;

    private String registryImageUrl;

    @Builder.Default
    private Integer cpuMax = 100;

    @Builder.Default
    private Integer memMax = 100;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
}
