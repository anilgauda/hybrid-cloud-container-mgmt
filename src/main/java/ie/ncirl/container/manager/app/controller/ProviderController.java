package ie.ncirl.container.manager.app.controller;

import ie.ncirl.container.manager.app.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProviderController {

    @Autowired
    private
    ProviderService providerService;

    @GetMapping(value = "/providers/view")
    public String show(Model model) {
        model.addAttribute("providers", providerService.getAllProviders());
        return "provider/view.html";
    }
}
