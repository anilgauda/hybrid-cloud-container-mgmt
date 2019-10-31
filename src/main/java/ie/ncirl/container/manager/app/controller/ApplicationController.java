package ie.ncirl.container.manager.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationController {

	@RequestMapping(value="/regapp")
	public String getApplicationRegisterd() {
		return "regapp";
	}
	
	@RequestMapping(value="/runapp")
	public String getRunningApplication() {
		return "runapp";
	}
}
