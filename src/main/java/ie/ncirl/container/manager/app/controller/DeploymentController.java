package ie.ncirl.container.manager.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DeploymentController {

	@RequestMapping(value="/deployapp")
	public String getDeployedApp() {
		return "deployapp";
	}
}
