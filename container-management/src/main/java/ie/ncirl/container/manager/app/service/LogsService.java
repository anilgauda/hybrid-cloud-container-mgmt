package ie.ncirl.container.manager.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.converters.LogsConvertor;
import ie.ncirl.container.manager.app.repository.LogsRepo;
import ie.ncirl.container.manager.common.domain.Logs;
import lombok.Getter;
import lombok.Setter;

@Service
@Setter
@Getter
public class LogsService {

	@Autowired
	LogsRepo logsRepo;
	
	@Autowired
	LogsConvertor logsConvertor;

	public void saveLogs(Logs logs) {
		logsRepo.save(logs);
	}

	public Page<Logs> getAllLogs(Pageable pageable){
		return logsRepo.findAll(pageable);
	}
}
