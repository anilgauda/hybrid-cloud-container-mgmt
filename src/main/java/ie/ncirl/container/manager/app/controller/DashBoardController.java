package ie.ncirl.container.manager.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import ie.ncirl.container.manager.app.service.DashboardService;

@Controller
public class DashBoardController {

	@Autowired
	DashboardService dashboardService;
	
	@RequestMapping(value = "/")
	public String getHome(Model model) {
		
		model.addAttribute("dashboardvo",dashboardService.getDashboardDetails());
		return "index";
	}

}
