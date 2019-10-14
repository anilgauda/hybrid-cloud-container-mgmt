package com.containermanagement.CMSystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashBoardController {

	@RequestMapping(value = "/")
	public String getDashBoard() {
		return "dashboard";
	}
}
