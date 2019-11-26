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
public class DeploymentModel {
	 private Container container;
	 private VM optimalVM;
}
