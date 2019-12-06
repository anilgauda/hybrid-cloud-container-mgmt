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


public class VMWeightedStrategy implements WeightedStrategy {
	
	public static Logger logger = Logger.getLogger(VMWeightedStrategy.class.getName());

	/**
	 * Deploy Application taking number of Virtual machine on which is to be deployed as a baseline to calculate the weight.
	 *
	 * @param deploymentList the deployment list
	 * @param weightInPercent the weight in percent
	 * @return the containers list
	 */
	@Override
	public ContainersList deploy(List<DeploymentModel> deploymentList, int weightInPercent) {
		ContainerConfig containerConfig = new ContainerConfig();
		Map<String, Integer> vmAppMap = new HashMap<>();
		Map<String,VMModel> vmMap=new HashMap<>();
		Map<String, Queue<ApplicationModel>> appVMMap = new HashMap<>();
		Map<String,Queue<VMModel>> appVMUndeployMap =new HashMap<>();
		Map<String,Queue<String>> appContainerMap= new HashMap<>();
		ArrayList<Container> deployedContainers=new ArrayList<>();
		ArrayList<Container> unDeployedContainers =new ArrayList<>();
		
		ContainersList containerList=new ContainersList();
		for (DeploymentModel model : deploymentList) {
			Container container = model.getContainer();
			ApplicationModel app = container.getApplication();
			VMModel deployVM = model.getOptimalVM();
			String vmName = deployVM.getName();

			if (vmAppMap.containsKey(vmName)) {
				vmAppMap.put(vmName, vmAppMap.get(vmName) + 1);
			} else {
				vmAppMap.put(vmName,1);
				vmMap.put(vmName, deployVM);
			}

			if (appVMMap.containsKey(vmName)) {
				Queue<ApplicationModel> deployList = appVMMap.get(vmName);
				deployList.add(app);
				appVMMap.put(vmName, deployList);
			} else {
				Queue<ApplicationModel> deployList = new LinkedList<>();
				deployList.add(app);
				appVMMap.put(vmName, deployList);
			}
			
			if (appVMUndeployMap.containsKey(app.getRegistryImageUrl())) {
				Queue<VMModel> unDeployList = appVMUndeployMap.get(app.getRegistryImageUrl());
				unDeployList.add(container.getServer());
				appVMUndeployMap.put(app.getRegistryImageUrl(), unDeployList);
			} else {
				Queue<VMModel> unDeployList = new LinkedList<>();
				unDeployList.add(container.getServer());
				appVMUndeployMap.put(app.getRegistryImageUrl(), unDeployList);
			}

			if (appContainerMap.containsKey(app.getRegistryImageUrl())) {
				Queue<String> containerIds = appContainerMap.get(app.getRegistryImageUrl());
				containerIds.add(container.getId());
				appContainerMap.put(app.getRegistryImageUrl(), containerIds);
			} else {
				Queue<String> containerIds = new LinkedList<>();
				containerIds.add(container.getId());
				appContainerMap.put(app.getRegistryImageUrl(), containerIds);
			}
		}
		logger.log(Level.INFO,"Undeploy map");
		appVMUndeployMap.forEach((key,value)-> {
		});
		vmAppMap.forEach((key,value) -> {
			
			logger.log(Level.INFO,String.format(" Weighted VM Statergy key: %s value: %s",key,value));
			/** Calculate the Number of server that is to be deployed first **/
			double numberOfVm = Math.ceil((float) (value.floatValue() * (weightInPercent / 100.0)));
			for (int i = 0; i < numberOfVm; i++) {
				Container deployedContainer=new Container();
				Container unDeployedContainer=new Container();
				ApplicationModel app = appVMMap.get(key).poll();
				VMModel vm =vmMap.get(key);
				if (vm != null) {
					try {
						deployedContainer.setId(containerConfig.startContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), app.getRegistryImageUrl()).get(0));
						deployedContainer.setServer(vm);
						deployedContainer.setApplication(app);
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Starting Container");
					}
				}
				vm=appVMUndeployMap.get(app.getRegistryImageUrl()).poll();
				String containerId=appContainerMap.get(app.getRegistryImageUrl()).poll();
				if (vm != null) {
					try {
						List<String>containerIDs=new ArrayList<>();
						containerIDs.add(containerId);
						containerConfig.stopContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(),containerIDs);
						unDeployedContainer.setId(containerId);
						unDeployedContainer.setServer(vm);
						unDeployedContainer.setApplication(app);
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Starting Container");
					}
				}
				vmAppMap.put(key, vmAppMap.get(key)-1);
				deployedContainers.add(deployedContainer);
				unDeployedContainers.add(unDeployedContainer);
			}
			
		});
		
		/***Application Sleep for health Checks**/
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			
		}
		logger.log(Level.INFO, String.format("current map val after calc: %s", vmAppMap.toString()));

		vmAppMap.forEach((key,value) -> {
			for (int i = 0; i < value; i++) {
				Container deployedContainer=new Container();
				Container unDeployedContainer=new Container();
				ApplicationModel app = appVMMap.get(key).poll();
				VMModel vm =vmMap.get(key);
				if (vm != null) {
					try {
						deployedContainer.setId(containerConfig.startContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), app.getRegistryImageUrl()).get(0));
						deployedContainer.setServer(vm);
						deployedContainer.setApplication(app);
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Starting Container");
					}
				}
				vm=appVMUndeployMap.get(app.getRegistryImageUrl()).poll();
				String containerId=appContainerMap.get(app.getRegistryImageUrl()).poll();
				if (vm != null) {
					try {
						List<String>containerIDs=new ArrayList<>();
						containerIDs.add(containerId);
						containerConfig.stopContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(),containerIDs);
						unDeployedContainer.setId(containerId);
						unDeployedContainer.setServer(vm);
						unDeployedContainer.setApplication(app);
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Starting Container");
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
