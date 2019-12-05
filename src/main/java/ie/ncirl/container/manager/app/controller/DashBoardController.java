package ie.ncirl.container.manager.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ie.ncirl.container.manager.app.service.DashboardService;

@Controller
public class DashBoardController {

	@Autowired
	DashboardService dashboardService;
	
	@GetMapping(value = "/")
	public String getHome(Model model) {
		
		model.addAttribute("dashboardvo",dashboardService.getDashboardDetails());
		return "index";
	}

}
