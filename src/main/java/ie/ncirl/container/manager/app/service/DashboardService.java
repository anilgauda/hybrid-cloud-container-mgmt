package ie.ncirl.container.manager.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.app.vo.DashboardVo;
import ie.ncirl.container.manager.app.vo.VirtualMachineVo;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.common.domain.enums.Role;
import ie.ncirl.container.manager.library.configurevm.VMConfig;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class
DashboardService {

	/** The application service. */
	@Autowired
	ApplicationService appService;

	/** The virtual machine service. */
	@Autowired
	VMService vmService;

	/** The provider service. */
	@Autowired
	ProviderService providerService;

	/** The container service. */
	@Autowired
	ContainerDeploymentService containerService;
	
	@Autowired
	VMConverter vmConvertor;

	/** The user utility */
	@Autowired
	UserUtil userUtil;
	
	Logger logger = LoggerFactory.getLogger(DashboardService.class);


	/**
	 * Gets the dashboard details and return dashboardvo which is overview of all
	 * running applications and registerd vm Application is displayed in terms of
	 * virtual machine as well get virtual machine stats and pass it with
	 * dashboardVo.
	 *
	 * @return the dashboard details
	 */
	public DashboardVo getDashboardDetails() {
		DashboardVo dashboardVo = new DashboardVo();
		VMConfig config = new VMConfig();
		int numberOfContainers = 0;
		int numberOfApplicationRunning = 0;
		List<RegisterApplicationDto> applicationsVos=appService.getApplicationsByUser();
		List<VM> vmList;
		if (userUtil.getCurrentUserRole().contains(Role.USER.name())) {
			vmList = vmConvertor.fromDTOList(vmService.getAllVMs());
			dashboardVo.setTotalVmNo(vmService.getAllVMs().size());
		}else {
			vmList = vmService.findAllVmByUserId(userUtil.getCurrentUser().getId());
			dashboardVo.setTotalVmNo(vmService.findByUserId(userUtil.getCurrentUser().getId()).size());
		}
		dashboardVo.setProviderNo(providerService.getAllProviders().size());
		for (RegisterApplicationDto application : applicationsVos) {
			List<ContainerDeployment> containers = containerService.getContainersByAppId(application.getId());
			numberOfContainers += containers.size();
		}
		List<VirtualMachineVo> vmVoList = new ArrayList<>();
		for (VM vm : vmList) {
			VirtualMachineVo vmVo = new VirtualMachineVo();
			vmVo.setNumOfContainers(vm.getContainerDeployments().size());
			Map<String, Integer> vmStats = new HashMap<>();
			try {
				vmStats = config.getVMStats(vm.getPrivateKey(), vm.getUsername(), vm.getHost());
			} catch (DockerException e) {
				logger.error("Error Occurred while retriving vm stats");
			}
			vmVo.setCpuUtil(vmStats.get("us").toString());
			vmVo.setMemUtil(String.valueOf((vmStats.get("free") / 1024)));
			vmVo.setVmName(vm.getName());
			vmVoList.add(vmVo);
		}
		dashboardVo.setTotalAppNo(numberOfApplicationRunning);
		dashboardVo.setVmList(vmVoList);
		dashboardVo.setTotalConNo(numberOfContainers);
		logger.debug("Dashboard Vo {}:",dashboardVo.toString());
		return dashboardVo;
	}
}
