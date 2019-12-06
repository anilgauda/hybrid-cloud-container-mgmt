package ie.ncirl.container.manager.library.configurevm.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import ie.ncirl.container.manager.library.configurevm.ContainerConfig;
import ie.ncirl.container.manager.library.configurevm.model.ApplicationModel;
import ie.ncirl.container.manager.library.configurevm.model.Container;
import ie.ncirl.container.manager.library.configurevm.model.ContainersList;
import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;
import ie.ncirl.container.manager.library.configurevm.model.VMModel;

public class ApplicationWeightedStrategy implements WeightedStrategy {


	public static Logger logger = Logger.getLogger(ApplicationWeightedStrategy.class.getName());

	/**
	 * Deploy Application taking number of applications to be deployed as a baseline to calculate the weight.
	 *
	 * @param deploymentList  the deployment list
	 * @param weightInPercent the weight in percent
	 * @return the containers list
	 */
	@Override
	public ContainersList deploy(List<DeploymentModel> deploymentList, int weightInPercent) {
		ContainerConfig containerConfig = new ContainerConfig();
		/**
		 * Map to store number of applications to be deployed key: repoName value:
		 * number of applicationData related to each repo
		 **/
		Map<String, Integer> appNumberMap = new HashMap<>();
		/**
		 * Map to store details of applicationData to be undeployed key : repoName value:
		 * queue of all vms to be undeployed
		 **/
		Map<String, Queue<VMModel>> appVMMapToDeploy = new HashMap<>();
		/**
		 * Map to store details of applicationData to be deployed key : repoName value:
		 * queue of all vms to be deployed
		 **/
		Map<String, Queue<VMModel>> appVMapToUndeploy = new HashMap<>();
		/**
		 * Map to store details of containerids to be undeployed : repoName value:
		 * queue of all containerids to be deployed
		 **/
		Map<String, Queue<String>> appContainerMap = new HashMap<>();
		Map<String,ApplicationModel> applicationMap=new HashMap<>();
		ArrayList<Container> deployedContainers=new ArrayList<>();
		ArrayList<Container> unDeployedContainers =new ArrayList<>();
		ContainersList containerList=new ContainersList();
		for (DeploymentModel model : deploymentList) {
			Container container = model.getContainer();
			ApplicationModel app = container.getApplication();
			VMModel undeployVM = container.getServer();
			VMModel deployVM = model.getOptimalVM();
			String imageName = app.getRegistryImageUrl();

			if (appNumberMap.containsKey(imageName)) {
				appNumberMap.put(imageName, appNumberMap.get(imageName) + 1);
			} else {
				appNumberMap.put(imageName, 1);
			}

			if (appVMapToUndeploy.containsKey(imageName)) {
				Queue<VMModel> undeployList = appVMapToUndeploy.get(imageName);
				undeployList.add(undeployVM);
				appVMapToUndeploy.put(imageName, undeployList);
			} else {
				Queue<VMModel> undeployList = new LinkedList<>();
				undeployList.add(undeployVM);
				appVMapToUndeploy.put(imageName, undeployList);
			}

			if (appVMMapToDeploy.containsKey(imageName)) {
				Queue<VMModel> deployList = appVMapToUndeploy.get(imageName);
				deployList.add(deployVM);
				appVMMapToDeploy.put(imageName, deployList);
			} else {
				Queue<VMModel> deployList = new LinkedList<>();
				deployList.add(deployVM);
				appVMMapToDeploy.put(imageName, deployList);
			}

			if (appContainerMap.containsKey(imageName)) {
				Queue<String> containerIds = appContainerMap.get(imageName);
				containerIds.add(container.getId());
				appContainerMap.put(imageName, containerIds);
			} else {
				Queue<String> containerIds = new LinkedList<>();
				containerIds.add(container.getId());
				appContainerMap.put(imageName, containerIds);
			}
				applicationMap.put(imageName, app);
			
		}
		/** Weighted value percent Deployment **/
		appNumberMap.forEach((key, value) -> {
			
			logger.log(Level.INFO, String.format(" Weighted Application Statergy key: %s value: %s", key, value));
			/** Calculate the Number of server that is to be deployed first **/
			double numberOfServer = Math.ceil((float) (value.floatValue() * (weightInPercent / 100.0)));
			logger.log(Level.INFO, String.format(" Number of Servers %s", numberOfServer));
			for (int i = 0; i < numberOfServer; i++) {
				// Deploy Vms
				Container deployedContainer=new Container();
				Container unDeployedContainer=new Container();
				VMModel vm = appVMMapToDeploy.get(key).poll();
				if (vm != null) {
					try {
						deployedContainer.setId(containerConfig.startContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), key).get(0));
						deployedContainer.setServer(vm);
						deployedContainer.setApplication(applicationMap.get(key));
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Starting  Container");
					}
				}
				// Undeploy Vms
				vm = appVMapToUndeploy.get(key).poll();
				String containerId = appContainerMap.get(key).poll();
				if (vm != null) {
					List<String> containerIDs = new ArrayList<>();
					containerIDs.add(containerId);
					try {
						containerConfig.stopContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), containerIDs);
						unDeployedContainer.setId(containerId);
						unDeployedContainer.setServer(vm);
						unDeployedContainer.setApplication(applicationMap.get(key));
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Stopping  Container");
					}
				}
				appNumberMap.put(key, appNumberMap.get(key) - 1);
				deployedContainers.add(deployedContainer);
				unDeployedContainers.add(unDeployedContainer);
			}

		});
		/*** Application Sleep for health Checks **/
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {

		}
		logger.log(Level.INFO, String.format("current map val after calc: %s", appNumberMap.toString()));

		/** Deploy remaining Application **/
		appNumberMap.forEach((key, value) -> {
			for (int i = 0; i < value; i++) {
				// Deploy Vms
				Container deployedContainer=new Container();
				Container unDeployedContainer=new Container();
				VMModel vm = appVMMapToDeploy.get(key).poll();
				if (vm != null) {
					try {
						deployedContainer.setId(containerConfig.startContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), key).get(0));
						deployedContainer.setServer(vm);
						deployedContainer.setApplication(applicationMap.get(key));
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Starting Container");
					}
				}
				// Undeploy Vms
				vm = appVMapToUndeploy.get(key).poll();
				String containerId = appContainerMap.get(key).poll();
				if (vm != null) {
					List<String> containerIDs = new ArrayList<>();
					containerIDs.add(containerId);
					try {
						containerConfig.stopContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), containerIDs);
						unDeployedContainer.setId(containerId);
						unDeployedContainer.setServer(vm);
						unDeployedContainer.setApplication(applicationMap.get(key));
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Stopping  Container");
					}
				}
				deployedContainers.add(deployedContainer);
				unDeployedContainers.add(unDeployedContainer);
			}
		});
		containerList.setDeployedContainers(deployedContainers);
		containerList.setUndeployedContainers(unDeployedContainers);
		return containerList;
	}
}
