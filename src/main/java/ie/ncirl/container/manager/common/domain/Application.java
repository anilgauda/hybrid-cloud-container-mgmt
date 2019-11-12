package ie.ncirl.container.manager.common.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications")
public class Application {
	
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;

    private String name;

    private String registryImageUrl;

    private Integer cpu; // In % of total VM CPU predicted to be used.. This is used to optimize allocation

    private Integer memory; // In MB of memory that app will use.. This is used in allocators

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
}
