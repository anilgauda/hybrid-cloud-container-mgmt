package ie.ncirl.container.manager.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.app.vo.DashboardVo;
import ie.ncirl.container.manager.app.vo.VirtualMachineVo;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.VMConfig;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;

@Service
public class DashboardService {

	@Autowired
	ApplicationService appService;

	@Autowired
	VMService vmService;

	@Autowired
	ProviderService providerService;

	@Autowired
	ContainerDeploymentService containerService;

	@Autowired
	UserUtil userUtil;

	public DashboardVo getDashboardDetails() {
		DashboardVo dashboardVo = new DashboardVo();
		VMConfig config = new VMConfig();
		int numberOfContainers = 0;
		int numberOfApplicationRunning=0;
		List<Application> applicationsVos = appService.getAllApplicationByUserId(userUtil.getCurrentUser().getId());
		List<VM> vmList=vmService.findAllVmByUserId(userUtil.getCurrentUser().getId());
		dashboardVo.setTotalVmNo(vmService.findByUserId(userUtil.getCurrentUser().getId()).size());
		dashboardVo.setProviderNo(providerService.getAllProviders().size());
		for (Application application : applicationsVos) {
			List<ContainerDeployment> containers = containerService.getContainersByAppId(application.getId());
				numberOfContainers += containers.size();				
		}
		List<VirtualMachineVo> vmVoList=new ArrayList<>();
		for(VM vm:vmList) {
			VirtualMachineVo vmVo=new VirtualMachineVo();
			vmVo.setNumOfContainers(vm.getContainerDeployments().size());
			Map<String,Integer> vmStats=new HashMap<>();
			try {
				vmStats=config.getVMStats(vm.getPrivateKey(), vm.getUsername(), vm.getHost());
			} catch (DockerException e) {
				System.out.println("Error Occurred while retriving vm stats");
			}
			vmVo.setCpuUtil(vmStats.get("us").toString());
			vmVo.setMemUtil(String.valueOf((vmStats.get("free")/1024)));
			vmVo.setVmName(vm.getName());
			vmVoList.add(vmVo);
		}
		dashboardVo.setTotalAppNo(numberOfApplicationRunning);
		dashboardVo.setVmList(vmVoList);
		dashboardVo.setTotalConNo(numberOfContainers);
		System.out.println(dashboardVo.toString());
		return dashboardVo;
	}
}
