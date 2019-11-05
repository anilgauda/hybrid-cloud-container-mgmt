package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.service.ProviderService;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.validator.ProviderValidator;
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

@Slf4j
@Controller
public class ProviderController {

    @Autowired
    private
    ProviderService providerService;

    @Autowired
    private
    ProviderValidator providerValidator;

    @GetMapping(value = "/providers/view")
    public String showProvidersListPage(Model model) {
        model.addAttribute("providers", providerService.getAllProviders());
        return "provider/view.html";
    }

    @GetMapping(value = "/provider/create")
    public String showCreateProviderPage(Model model) {
        model.addAttribute("provider", new Provider());
        return "provider/create.html";
    }

    @GetMapping(value = "/provider/{id}/edit")
    public String showEditProviderPage(Model model, @PathVariable("id") String id) {
        model.addAttribute("provider", providerService.findById(Long.valueOf(id)));
        return "provider/edit.html";
    }

    @PostMapping(value = "/provider")
    public String createProvider(@Valid @ModelAttribute Provider provider, BindingResult result, RedirectAttributes redirectAttributes) {
        log.info(String.format("Creating provider %s", provider));
        providerValidator.validate(provider, result);
        if (result.hasErrors()) {
            return "/provider/create";
        }

        providerService.save(provider);
        redirectAttributes.addFlashAttribute("message", String.format("Provider %s created successfully", provider.getName()));
        return "redirect:/providers/view";
    }

    @PostMapping(value = "/provider/edit")
    public String editProvider(@Valid @ModelAttribute Provider provider, BindingResult result, RedirectAttributes redirectAttributes) {
        log.info(String.format("Editing provider %s", provider));
        providerValidator.validate(provider, result);
        if (result.hasErrors()) {
            return "/provider/edit";
        }

        providerService.save(provider);
        redirectAttributes.addFlashAttribute("message", String.format("Provider %s edited successfully", provider.getName()));
        return "redirect:/providers/view";
    }

    @PostMapping(value = "/provider/{id}/delete")
    public String deleteProvider(@PathVariable("id") String id) {
        log.info(String.format("Deleting provider %s", id));

        providerService.delete(Long.valueOf(id));
        return "redirect:/providers/view";
    }
}
