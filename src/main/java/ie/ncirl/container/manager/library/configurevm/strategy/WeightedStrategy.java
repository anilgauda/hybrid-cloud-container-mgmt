package ie.ncirl.container.manager.library.configurevm.strategy;

import java.util.List;

import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;

public interface WeightedStrategy {

	void deploy(List<DeploymentModel> deploymentList, int weightInPercent);

}