package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.service.ApplicationService;
import ie.ncirl.container.manager.app.service.ContainerDeploymentService;
import ie.ncirl.container.manager.app.service.VMService;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.app.vo.DeploymentVo;
import ie.ncirl.container.manager.common.domain.enums.DeploymentType;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Arrays;

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


    @RequestMapping(value = "/deployapp")
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
            deploymentService.deployContainers(allocation.getApplication(),
                    allocation.getServer(),
                    Integer.parseInt(deploymentVo.getDeploymentType()));
        }

        redirectAttributes.addFlashAttribute("message", "Deployment created successfully");

        return "redirect:/deployapp";
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
