package ie.ncirl.container.manager.library.configurevm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
	 private Long id;
	    private String name;

	    private String registryImageUrl;

	    private Integer cpu = 0; // In % of total VM CPU predicted to be used.. This is used to optimize allocation

	    private Integer memory = 0; // In MB of memory that app will use.. This is used in allocators

		
}
