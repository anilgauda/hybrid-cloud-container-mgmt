package ie.ncirl.container.manager.app.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ie.ncirl.container.manager.app.dto.PageData;
import ie.ncirl.container.manager.app.service.LogsService;
import ie.ncirl.container.manager.app.util.PageUtil;
import ie.ncirl.container.manager.common.domain.Logs;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LogsController {

	@Autowired
	LogsService logsService;

	@GetMapping(value = "/logs/view")
	public String getLogs(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		int currentPage = page.orElse(1);
		int pageSize = size.orElse(10);
		Page<Logs> logsPage = logsService.getAllLogs(PageRequest.of(currentPage - 1, pageSize));
		List<Logs> logs = logsPage.toList();
		model.addAttribute("logs", logs);
		log.info("Logs: {}",logs);
		PageData pageData = PageUtil.getPageData(logsPage);
		model.addAttribute("pageNumbers", pageData.getPageNumbers());
		model.addAttribute("currPage", pageData.getCurrPage());
		model.addAttribute("totalPages", pageData.getTotalPages());
		return "logs/view";
	}
}
