package ie.ncirl.container.manager.common.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Table(name = "applications")
public class Application {

    @Id
    private Long id;

    private String name;

    private String registryImageUrl;

    private Integer cpu; // In % of total VM CPU predicted to be used.. This is used to optimize allocation

    private Integer memory; // In MB of memory that app will use.. This is used in allocators

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
}
