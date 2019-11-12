package ie.ncirl.container.manager.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterApplicationDto implements DTO{

	private static final long serialVersionUID = 1L;
	private Long Id;
	private String name;
	private String registryImageUrl;
	private Integer cpu;
	private Integer memory;
}
