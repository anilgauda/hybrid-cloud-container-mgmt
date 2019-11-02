package ie.ncirl.container.manager.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VMController {

    @GetMapping(value = "/vms/view")
    public String getProviders() {
        return "vm/view.html";
    }
}
