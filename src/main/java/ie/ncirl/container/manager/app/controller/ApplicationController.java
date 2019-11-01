package ie.ncirl.container.manager.app.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ie.ncirl.container.manager.app.dto.Application;
import ie.ncirl.container.manager.app.service.VMService;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.ConfigureVM;
import ie.ncirl.container.manager.library.configurevm.exception.DockerInstallationException;

@Controller
public class ApplicationController {
	
	Logger log= LoggerFactory.getLogger(ApplicationController.class);
	@Autowired
	VMService vmService;

	@RequestMapping(value="/regapp")
	public String getApplicationRegisterd() {
		return "regapp";
	}
	
	@RequestMapping(value="/runapp")
	public String getRunningApplication(Model model) throws IOException, DockerInstallationException {
		List<VM> listOfVms=vmService.getAllVM();//need to get vm baised on user
		ConfigureVM config=new ConfigureVM();		
		log.info("List of Vms's :{} ",vmService.getAllVM().toString());
		List<Application> applications=new ArrayList<>();
		for(VM vm:listOfVms) {
			ArrayList<String> linuxContainers=config.getContainerIds(vm.getPrivateKey(), vm.getUsername(), vm.getHost());
			for(String containerId:linuxContainers) {
				Application app=new Application();
				Map<String,String> containerStats=config.getContainerStats(vm.getPrivateKey(), vm.getUsername(), vm.getHost(),containerId);
				app.setContainerId(containerId);
				app.setContainerStats(containerStats);
				applications.add(app);
			}
		}	
		model.addAttribute("applications",applications);
		return "runapp";
	}
}
