package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.service.ApplicationService;
import ie.ncirl.container.manager.app.service.ContainerDeploymentService;
import ie.ncirl.container.manager.app.service.VMService;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.app.vo.DeploymentVo;
import ie.ncirl.container.manager.common.domain.enums.AppDeployStrategy;
import ie.ncirl.container.manager.common.domain.enums.DeploymentType;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
public class DeploymentController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ContainerDeploymentService deploymentService;

    @Autowired
    private VMService vmService;

    @Autowired
    private UserUtil userUtil;


    @GetMapping(value = "/deployapp")
    public String getDeployedApp(Model model) {
        addModelAttributesForDeployApp(model);
        model.addAttribute("deploymentVo", new DeploymentVo());
        return "deployapp";
    }

    @PostMapping(value = "/deploy/{Id}")
    public String deployApplicationWithType(@Valid @ModelAttribute DeploymentVo deploymentVo, BindingResult result,
                                            RedirectAttributes redirectAttributes,
                                            Model model, @PathVariable("Id") String appId) {
        log.info(String.format("Deployment for appId: %s, For VM: %s, with type: %s", appId, deploymentVo.getVmIds(),
                deploymentVo.getDeploymentType()));

        if (result.hasErrors()) {
            addModelAttributesForDeployApp(model);
            return "deployapp";
        }

        AllocationData allocationData = deploymentService.getAllocationData(appId, deploymentVo);

        if (allocationData.getFailedAllocations() > 0) {
            result.rejectValue("allocation", "failed.allocation",
                    String.format("Servers selected do not have enough capacity for required deployment. " +
                            "No. of failed allocations: %d", allocationData.getFailedAllocations()));
            addModelAttributesForDeployApp(model);
            return "deployapp";
        }

        log.info(String.format("Allocation data for deploying app %s is %s", appId, allocationData));

        for (Allocation allocation : allocationData.getAllocations()) {
            try {
				deploymentService.deployContainers(allocation.getApplicationData(),
				        allocation.getServer(),
				        allocation.getCount());
			} catch (ContainerException e) {
				log.error("Invalid Repository Name ",e);
				 redirectAttributes.addFlashAttribute("delMessage", e.getMessage());
			      return "redirect:/deployapp";
			}
        }

        redirectAttributes.addFlashAttribute("message", "Deployment created successfully");

        return "redirect:/deployapp";
    }

    @GetMapping("/deploy/optimize")
    public String showOptimizeView(Model model) {
        model.addAttribute("vms", vmService.findAllVmByUserId(userUtil.getCurrentUser().getId()));
        model.addAttribute("strategy",Arrays.asList(AppDeployStrategy.values()));
        return "optimize/view";
    }

    @PostMapping("/deploy/optimize/check")
    public String showOptimizeCheckView(@RequestParam("vms") List<String> vmIds, @RequestParam("deploymentStrategy") String strategy,@RequestParam("weight") String weight,Model model) {
        model.addAttribute("optimizations", deploymentService.getOptimizationChanges(vmService.findByVmIds(vmIds)));
        model.addAttribute("vmIds", vmIds);
        model.addAttribute("deploymentStrategy",strategy);
        model.addAttribute("weight",weight);

        return "optimize/check";
    }

    @PostMapping("/deploy/optimize")
    public String optimizeContainers(@RequestParam("vmIds") List<String> vmIds,@RequestParam("deploymentStrategy") String strategy,@RequestParam("weight") String weight, Model model, RedirectAttributes redirectAttributes) {
        try {
			deploymentService.optimizeContainers(vmService.findByVmIds(vmIds),Integer.parseInt(strategy),Integer.parseInt(weight));
		} catch (ContainerException e) {
			log.error("Invalid Repository Name:",e);
		}
        redirectAttributes.addFlashAttribute("message", "Containers/VMs optimized successfully");
        return "redirect:/deploy/optimize";
    }


    /**
     * These are common attributes that are always added when showing deploy app page
     *
     * @param model Spring Model
     */
    private void addModelAttributesForDeployApp(Model model) {
        model.addAttribute("regApplication", applicationService.getApplicationsByUser());
        model.addAttribute("availableVms", vmService.findByUserId(userUtil.getCurrentUser().getId()));
        model.addAttribute("availableTypes", Arrays.asList(DeploymentType.values()));
    }

}
