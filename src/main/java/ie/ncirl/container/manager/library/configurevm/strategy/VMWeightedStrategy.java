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
import ie.ncirl.container.manager.library.configurevm.model.Application;
import ie.ncirl.container.manager.library.configurevm.model.Container;
import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;
import ie.ncirl.container.manager.library.configurevm.model.VM;

public class VMWeightedStrategy implements WeightedStrategy {
	public static Logger logger = Logger.getLogger(VMWeightedStrategy.class.getName());

	@Override
	public void deploy(List<DeploymentModel> deploymentList, int weightInPercent) {
		ContainerConfig containerConfig = new ContainerConfig();
		Map<String, Integer> vmAppMap = new HashMap<>();
		Map<String,VM> vmMap=new HashMap<>();
		Map<String, Queue<Application>> appVMMap = new HashMap<>();
		Map<String,Queue<VM>> appVMUndeployMap =new HashMap<>();
		Map<String,Queue<String>> appContainerMap= new HashMap<>();
		
		for (DeploymentModel model : deploymentList) {
			Container container = model.getContainer();
			Application app = container.getApplication();
			VM deployVM = model.getOptimalVM();
			String vmName = deployVM.getName();

			if (vmAppMap.containsKey(vmName)) {
				vmAppMap.put(vmName, vmAppMap.get(vmName) + 1);
			} else {
				vmAppMap.put(vmName,1);
				vmMap.put(vmName, deployVM);
			}

			if (appVMMap.containsKey(vmName)) {
				Queue<Application> deployList = appVMMap.get(vmName);
				deployList.add(app);
				appVMMap.put(vmName, deployList);
			} else {
				Queue<Application> deployList = new LinkedList<>();
				deployList.add(app);
				appVMMap.put(vmName, deployList);
			}
			
			if (appVMUndeployMap.containsKey(app.getRegistryImageUrl())) {
				Queue<VM> unDeployList = appVMUndeployMap.get(app.getRegistryImageUrl());
				unDeployList.add(container.getServer());
				appVMUndeployMap.put(app.getRegistryImageUrl(), unDeployList);
			} else {
				Queue<VM> unDeployList = new LinkedList<>();
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
		System.out.println("before Weighted Deployment map: "+vmAppMap.toString());
		vmAppMap.forEach((key,value) -> {
			logger.log(Level.INFO,String.format(" Weighted VM Statergy key: %s value: %s",key,value));
			double numberOfVm = Math.ceil((float) (value.floatValue() * (weightInPercent / 100.0)));
			System.out.println("Number of servers to deploy"+numberOfVm);
			for (int i = 0; i < numberOfVm; i++) {
				Application app = appVMMap.get(key).poll();
				VM vm =vmMap.get(key);
				if (vm != null) {
					try {
						containerConfig.startContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), app.getRegistryImageUrl());
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
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Starting Container");
					}
				}
				vmAppMap.put(key, vmAppMap.get(key)-1);
			}
			
		});
		
		/***Application Sleep for health Checks**/
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			
		}
		System.out.println("after Weighted Deployment map: "+vmAppMap.toString());

		vmAppMap.forEach((key,value) -> {
			for (int i = 0; i < value; i++) {
				Application app = appVMMap.get(key).poll();
				VM vm =vmMap.get(key);
				if (vm != null) {
					try {
						containerConfig.startContainers(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), app.getRegistryImageUrl());
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
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error Occured While Starting Container");
					}
				}
			}
		});
	}

}
