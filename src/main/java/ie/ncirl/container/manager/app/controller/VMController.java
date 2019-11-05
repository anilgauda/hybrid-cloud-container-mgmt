package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.service.VMService;
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
    private
    VMService vmService;


    @Autowired
    private
    VMValidator vmValidator;

    @GetMapping(value = "/vms/view")
    public String showListPage(Model model) {
        model.addAttribute("vms", vmService.getAllVMs());
        return "vm/view.html";
    }

    @GetMapping(value = "/vms/create")
    public String showCreatePage(Model model) {
        model.addAttribute("vm", new VMDTO());
        return "vm/create.html";
    }

    @GetMapping(value = "/vm/{id}/edit")
    public String showEditPage(Model model, @PathVariable("id") String id) {
        model.addAttribute("vm", vmService.findById(Long.valueOf(id)));
        return "vm/edit.html";
    }

    @PostMapping(value = "/vm")
    public String create(@Valid @ModelAttribute VMDTO vmDto, BindingResult result, RedirectAttributes redirectAttributes) {
        log.info(String.format("Creating VM %s", vmDto));
        vmValidator.validate(vmDto, result);
        if (result.hasErrors()) {
            return "/vm/create";
        }

        vmService.save(vmDto);
        redirectAttributes.addFlashAttribute("message", String.format("VM %s created successfully", vmDto.getName()));
        return "redirect:/vms/view";
    }

    @PostMapping(value = "/vm/edit")
    public String edit(@Valid @ModelAttribute VMDTO vmDto, BindingResult result, RedirectAttributes redirectAttributes) {
        log.info(String.format("Editing vm %s", vmDto));
        vmValidator.validate(vmDto, result);
        if (result.hasErrors()) {
            return "/vm/edit";
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
}
