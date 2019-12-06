package ie.ncirl.container.manager.library.configurevm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VMModel {
	 private Long id;
	    private String name;
	    private String host;
	    private String username;
	    private byte[] privateKey;
	    private Integer memory;
		
}
