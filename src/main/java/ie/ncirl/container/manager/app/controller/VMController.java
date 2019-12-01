package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.service.ProviderService;
import ie.ncirl.container.manager.app.service.VMService;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.common.domain.enums.Role;
import ie.ncirl.container.manager.common.domain.validator.VMValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@Slf4j
public class VMController {

	@Autowired
	private VMService vmService;

	@Autowired
	private VMValidator vmValidator;

	@Autowired
	private ProviderService providerService;

	@Autowired
	private UserUtil userUtil;

	@Autowired
	private VMConverter vmConvertor;

	@GetMapping(value = "/vms/view")
	public String showListPage(Model model) {
		System.out.println("Compare Current role: "+userUtil.getCurrentUserRole());
		System.out.println("Enum Role: "+ Role.USER.toString());
		if (userUtil.getCurrentUserRole().contains(Role.USER.toString())) {
			model.addAttribute("vms", vmService.getAllVMs());
		} else {
			model.addAttribute("vms", vmConvertor.fromDomainList(vmService.findAllVmByUserId(userUtil.getCurrentUser().getId())));
		}
		return "vm/view.html";
	}

	@GetMapping(value = "/vm/create")
	public String showCreatePage(Model model) {
		model.addAttribute("vm", new VMDTO());
		return renderCreatePage(model);
	}

	@GetMapping(value = "/vm/{id}/edit")
	public String showEditPage(Model model, @PathVariable("id") String id) {
		return renderEditPage(model, vmService.findById(Long.valueOf(id)));
	}

	@PostMapping(value = "/vm")
	public String create(@Valid @ModelAttribute("vm") VMDTO vmDto, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		log.info(String.format("Creating VM %s", vmDto));
		vmValidator.validate(vmDto, result);
		if (result.hasErrors()) {
			return renderCreatePage(model);
		}

		vmService.save(vmDto);
		redirectAttributes.addFlashAttribute("message", String.format("VM %s created successfully", vmDto.getName()));
		return "redirect:/vms/view";
	}

	@PostMapping(value = "/vm/edit")
	public String edit(@Valid @ModelAttribute("vmDto") VMDTO vmDto, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		log.info(String.format("Editing vm %s", vmDto));
		vmValidator.validate(vmDto, result);
		if (result.hasErrors()) {
			return renderEditPage(model, vmDto);
		}

		vmService.save(vmDto);
		redirectAttributes.addFlashAttribute("message", String.format("VM %s edited successfully", vmDto.getName()));
		return "redirect:/vms/view";
	}

	@PostMapping(value = "/vm/{id}/delete")
	public String delete(@PathVariable("id") String id) {
		log.info(String.format("Deleting VM with id %s", id));

		vmService.delete(Long.valueOf(id));
		return "redirect:/vms/view";
	}

	/**
	 * Renders create page with any required dependent data
	 *
	 * @param model Spring model attributes used in thymeleaf
	 * @return the view page
	 */
	private String renderCreatePage(Model model) {
		model.addAttribute("availableProviders", providerService.getAllProviders());
		return "vm/create";
	}

	/**
	 * Renders edit page with any required dependent data
	 *
	 * @param model Spring model attributes used in thymeleaf
	 * @return the view page
	 */
	private String renderEditPage(Model model, VMDTO vmdto) {
		model.addAttribute("vmDto", vmdto);
		model.addAttribute("availableProviders", providerService.getAllProviders());
		return "vm/edit";
	}

}
