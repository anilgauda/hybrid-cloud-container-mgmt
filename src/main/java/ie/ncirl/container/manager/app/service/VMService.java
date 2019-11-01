package ie.ncirl.container.manager.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.repository.VMRepo;
import ie.ncirl.container.manager.common.domain.VM;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VMService {
	@Autowired
	VMRepo vmRepo;
	
	public List<VM> getAllVM() {
		return vmRepo.findAll();
	}
	
	public void saveVMData(VM vmData) {
		vmRepo.save(vmData);
	}
}
