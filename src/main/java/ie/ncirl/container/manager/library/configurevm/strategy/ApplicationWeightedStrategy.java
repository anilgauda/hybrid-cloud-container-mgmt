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
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.configurevm.model.Application;
import ie.ncirl.container.manager.library.configurevm.model.Container;
import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;
import ie.ncirl.container.manager.library.configurevm.model.VM;

public class ApplicationWeightedStrategy implements WeightedStrategy {

	public static Logger logger = Logger.getLogger(ApplicationWeightedStrategy.class.getName());

	@Override
	public void deploy(List<DeploymentModel> deploymentList, int weightInPercent) {
		ContainerConfig containerConfig = new ContainerConfig();
		Map<String, Integer> appNumberMap = new HashMap<>();
		Map<String, Queue<VM>> appVMMapToDeploy = new HashMap<>();
		Map<String, Queue<VM>> appVMapToUndeploy = new HashMap<>();
		Map<String,Queue<String>> appContainerMap= new HashMap<>();
		for (DeploymentModel model : deploymentList) {
			Container container = model.getContainer();
			Application app = container.getApplication();
			VM undeployVM = container.getServer();
			VM deployVM = model.getOptimalVM();
			String imageName = app.getRegistryImageUrl();

			if (appNumberMap.containsKey(imageName)) {
				appNumberMap.put(imageName, appNumberMap.get(imageName) + 1);
			} else {
				appNumberMap.put(imageName, 1);
			}

			if (appVMapToUndeploy.containsKey(imageName)) {
				Queue<VM> undeployList = appVMapToUndeploy.get(imageName);
				undeployList.add(undeployVM);
				appVMapToUndeploy.put(imageName, undeployList);
			} else {
				Queue<VM> undeployList = new LinkedList<>();
				undeployList.add(undeployVM);
				appVMapToUndeploy.put(imageName, undeployList);
			}

			if (appVMMapToDeploy.containsKey(imageName)) {
				Queue<VM> deployList = appVMapToUndeploy.get(imageName);
				deployList.add(deployVM);
				appVMMapToDeploy.put(imageName, deployList);
			} else {
				Queue<VM> deployList = new LinkedList<>();
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
		}
		System.out.println("current map: "+appNumberMap.toString());
		/**Weighted value percent Deployment **/
		appNumberMap.forEach((key, value) -> {
			logger.log(Level.INFO,String.format(" Weighted Application Statergy key: %s value: %s",key,value));		
			double numberOfServer =Math.ceil((float) (value.floatValue() * (weightInPercent / 100.0)));
			logger.log(Level.INFO,String.format(" Number of Servers %s",numberOfServer));			
			for (int i = 0; i < numberOfServer; i++) {
				// Deploy Vms
				VM vm = appVMMapToDeploy.get(key).poll();
				if (vm != null) {
						try {
							containerConfig.startContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), key);
						} catch (Exception e) {
							logger.log(Level.SEVERE, "Error Occured While Starting  Container");
							}
				}
				// Undeploy Vms
				vm = appVMapToUndeploy.get(key).poll();
				String containerId=appContainerMap.get(key).poll();
				if (vm != null) {
					List<String>containerIDs=new ArrayList<>();
					containerIDs.add(containerId);
					try {
						containerConfig.stopContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(),containerIDs);
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Stopping  Container");
					}
				}
				appNumberMap.put(key, appNumberMap.get(key)-1);
			}
			
		});
		/***Application Sleep for health Checks**/
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			
		}
		System.out.println("current map val after calc: "+appNumberMap.toString());
		/**Deploy remaining Application**/
		appNumberMap.forEach((key, value) -> {
			for (int i = 0; i < value; i++) {
				// Deploy Vms
				VM vm = appVMMapToDeploy.get(key).poll();
				if (vm != null) {
					try {
						containerConfig.startContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), key);
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Starting Container");
					}
				}
				// Undeploy Vms
				vm = appVMapToUndeploy.get(key).poll();
				String containerId=appContainerMap.get(key).poll();
				if (vm != null) {
					List<String>containerIDs=new ArrayList<>();
					containerIDs.add(containerId);
					try {
						containerConfig.stopContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(),containerIDs);
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Stopping  Container");
					}
				}
			}
		});
	}
}
