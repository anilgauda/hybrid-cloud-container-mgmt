package ie.ncirl.container.manager.library.configurevm.strategy;

import java.util.List;

import ie.ncirl.container.manager.library.configurevm.model.ContainersList;
import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;


public class DeploymentStrategy {

	private WeightedStrategy strategy;

	/**
	 * Execute method used to call respective deployment strategy.
	 *
	 * @param deploymentList the deployment list
	 * @param weightInPercent the weight in percent
	 * @return 
	 */
	public ContainersList execute(List<DeploymentModel> deploymentList, int weightInPercent) {
		 return strategy.deploy(deploymentList, weightInPercent);
	}

	/**
	 * Sets the strategy.
	 *
	 * @param strategy the new strategy
	 */
	public void setStrategy(WeightedStrategy strategy) {
		this.strategy = strategy;
	}
	
}
