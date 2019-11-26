package ie.ncirl.container.manager.library.configurevm.strategy;

import java.util.List;

import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;

public class DeploymentStrategy {

	private WeightedStrategy strategy;

	public void execute(List<DeploymentModel> deploymentList, int weightInPercent) {
		 strategy.deploy(deploymentList, weightInPercent);
	}

	public void setStrategy(WeightedStrategy strategy) {
		this.strategy = strategy;
	}
	
}
