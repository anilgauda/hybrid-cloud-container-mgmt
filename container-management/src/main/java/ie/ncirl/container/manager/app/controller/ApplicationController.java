package ie.ncirl.container.manager.app.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.service.ApplicationService;
import ie.ncirl.container.manager.common.domain.validator.RegisterApplicationValidator;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@Autowired
	private RegisterApplicationValidator validator;
	

	@GetMapping(value = "/regapp")
	public String getApplicationRegistered(Model model) {
		model.addAttribute("regApplication", new RegisterApplicationDto());
		return "application/regapp";
	}

	@PostMapping(value = "/regapp")
	public String submitApplication(@Valid @ModelAttribute RegisterApplicationDto regApplication, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
		validator.validate(regApplication, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("regApplication", regApplication);
			model.addAttribute("message",  String.format("Application with name %s already Exist",regApplication.getName()));
			return "application/regapp";
		}
		applicationService.saveApplication(regApplication);
		redirectAttributes.addFlashAttribute("message", String.format("Application %s saved successfully", regApplication.getName()));
		return "redirect:/applicationList";
	}

	@GetMapping(value = "/runapp")
	public String getRunningApplications(Model model) throws IOException, ContainerException {
		model.addAttribute("applications", applicationService.getRunningApplication());
		return "application/runapp";
	}

	@GetMapping(value = "/applicationList")
	public String getApplicationList(Model model) {

		model.addAttribute("regApplication", applicationService.getApplicationsByUser());
		return "application/applicationList";

	}
	
	  @GetMapping(value = "/application/{id}/edit")
	    public String editApplication(Model model,@PathVariable("id")String id) {
		 log.debug("Edit Application");
		  model.addAttribute("regApplication", applicationService.getApplicationById(Long.parseLong(id)));
			return "application/editapp";
	    }

	    @PostMapping(value = "/application/{id}/delete")
	    public String deleteApplication(@PathVariable("id") String id,RedirectAttributes redirectAttributes) {
	    	 log.debug("Delete Application");
	    	 RegisterApplicationDto regApp=applicationService.getApplicationById(Long.parseLong(id));
	    		try {
					applicationService.deleteApplicationById(Long.parseLong(id));
				} catch (Exception e) {
			    	redirectAttributes.addFlashAttribute("delMessage", String.format("Unable to delete application %s containers are already running", regApp.getName()));
			    	return "redirect:/applicationList";
				}
		    	redirectAttributes.addFlashAttribute("delMessage", String.format("Application %s Deleted successfully", regApp.getName()));
			return "redirect:/applicationList";
	      
	    }
	    @PostMapping(value = "/editSave")
		public String editApplication(@Valid @ModelAttribute RegisterApplicationDto regApplication, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
			applicationService.saveApplication(regApplication);
			redirectAttributes.addFlashAttribute("message", String.format("Application %s saved successfully", regApplication.getName()));
			return "redirect:/applicationList";
		}
	    
	    @GetMapping(value = "/application/{appId}/stop")
	    public String deleteContainer(@PathVariable("appId") String appId,RedirectAttributes redirectAttributes) {
	    	 log.debug("Stop Application"+ Long.parseLong(appId));
	    	applicationService.stopApplication(Long.parseLong(appId));
			return "redirect:/runapp";
	      
	    }
}
