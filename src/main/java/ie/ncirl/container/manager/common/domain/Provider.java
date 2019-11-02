package ie.ncirl.container.manager.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Cloud providers like AWS, Azure, etc
 */
@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "providers")
public class Provider {

    @Id
    private Long id;

    private String name;
}
