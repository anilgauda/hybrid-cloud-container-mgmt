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

    @PostMapping(value = "/provider")
    public String createProvider(@Valid @ModelAttribute Provider provider, BindingResult result, RedirectAttributes redirectAttributes) {
        providerValidator.validate(provider, result);
        if (result.hasErrors()) {
            return "/provider/create";
        }

        providerService.save(provider);
        redirectAttributes.addFlashAttribute("message", String.format("Provider %s created successfully", provider.getName()));
        log.info(String.format("Creating provider %s", provider));
        return "redirect:/providers/view";
    }

}
