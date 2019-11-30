package ie.ncirl.container.manager.library.configurevm.model;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainersList {
 ArrayList<Container> deployedContainers;
 ArrayList<Container> undeployedContainers;
}
