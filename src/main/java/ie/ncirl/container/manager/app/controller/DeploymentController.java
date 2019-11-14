package ie.ncirl.container.manager.app.controller;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.service.ApplicationService;
import ie.ncirl.container.manager.app.service.ContainerDeploymentService;
import ie.ncirl.container.manager.app.service.ProviderService;
import ie.ncirl.container.manager.app.service.VMService;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.app.vo.DeploymentVo;
import ie.ncirl.container.manager.common.domain.enums.DeploymentType;

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
	
	@RequestMapping(value="/deployapp")
	public String getDeployedApp(Model model) {
		model.addAttribute("regApplication", applicationService.getApplicationsByUser());
		model.addAttribute("availableVms",vmService.findByUserId(userUtil.getCurrentUser().getId()));
		model.addAttribute("availableTypes", Arrays.asList(DeploymentType.values()));
		model.addAttribute("deploymentVo", new DeploymentVo());
		return "deployapp";
	}
	@PostMapping(value="/deploy/{Id}")
	public String deployApplicationWithType(@Valid @ModelAttribute DeploymentVo deploymentVo,@PathVariable("Id")String appId,RedirectAttributes redirectAttributes) {
		System.out.println(appId);
		System.out.println(deploymentVo.getVmId());
		System.out.println(deploymentVo.getDeploymentType());
		
		deploymentService.deployContainers(Long.parseLong(appId),Long.parseLong(deploymentVo.getVmId()),Integer.parseInt(deploymentVo.getDeploymentType()));
		//deploymentService.deployContainers(Long.parseLong(appId));
		return"redirect:/deployapp";
	}
}
