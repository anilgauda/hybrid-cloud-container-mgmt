package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.dto.Application;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.service.VMService;
import ie.ncirl.container.manager.library.configurevm.ContainerConfig;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class ApplicationController {

    @Autowired
    VMService vmService;

    @RequestMapping(value = "/regapp")
    public String getApplicationRegistered() {
        return "regapp";
    }

    @RequestMapping(value = "/runapp")
    public String getRunningApplications(Model model) throws IOException, ContainerException {
        List<VMDTO> listOfVms = vmService.getAllVMs();//need to get vm baised on user
        ContainerConfig config = new ContainerConfig();
        log.info("List of Vms's :{} ", vmService.getAllVMs().toString());
        List<Application> applications = new ArrayList<>();
        for (VMDTO vm : listOfVms) {
            ArrayList<String> linuxContainers = config.getContainerIds(vm.getPrivateKey(), vm.getUsername(), vm.getHost());
            for (String containerId : linuxContainers) {
                Application app = new Application();
                Map<String, String> containerStats = config.getContainerStats(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), containerId);
                app.setContainerId(containerId);
                app.setContainerStats(containerStats);
                applications.add(app);
            }
        }
        model.addAttribute("applications", applications);
        return "runapp";
    }
}
