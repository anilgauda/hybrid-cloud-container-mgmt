package ie.ncirl.container.manager.library.configurevm.strategy;

import java.util.List;

import ie.ncirl.container.manager.library.configurevm.model.ContainersList;
import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;

/**
 * The Interface WeightedStrategy. 
 * This Strategy is Inspired from 
 * https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/routing-policy.html
 * 
 */
public interface WeightedStrategy {

	/**
	 * Deploy.
	 *
	 * @param deploymentList the deployment list
	 * @param weightInPercent the weight in percent
	 */
	ContainersList deploy(List<DeploymentModel> deploymentList, int weightInPercent);

}