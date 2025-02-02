package ie.ncirl.container.manager.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.app.repository.VMRepo;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.common.domain.Logs;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.common.domain.logging.Log;
import ie.ncirl.container.manager.common.domain.logging.VmLogs;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VMService {
    @Autowired
    private
    VMRepo vmRepo;

    @Autowired
    private
    ProviderRepo providerRepo;

    @Autowired
    private
    VMConverter converter;

    @Autowired
    private
    UserUtil userUtil;

    @Autowired
    private VMClient vmClient;
    
    @Autowired
    private LogsService logservice;
    
    public List<VMDTO> getAllVMs() {
        return converter.fromDomainList(vmRepo.findAll());
    }

    public List<VM> findAllVMs() {
        return vmRepo.findAll();
    }


    public List<VM> findByVmIds(List<String> vmIds) {
        return vmRepo.findByIdIn(vmIds.stream().map(Long::new).collect(Collectors.toList()));
    }

    public VMDTO findByName(String name) {
        return converter.from(vmRepo.findByName(name));
    }

    public VM findVMById(Long id) {
        return vmRepo.getOne(id);
    }

    public VMDTO findById(Long id) {
        VM vm = vmRepo.getOne(id);
        VMDTO vmdto = converter.from(vm);
        vmdto.setProviderId(vm.getProvider().getId());
        return vmdto;
    }

    public void save(VMDTO vmDTO) {
        VM vm = converter.from(vmDTO);
        vm.setUser(userUtil.getCurrentUser());
        vm.setProvider(providerRepo.findById(vmDTO.getProviderId()).get());

        if (vmDTO.getId() == null) {
            vm.setMemory(vmClient.getAvailableMemory(vm));
        }
        
        vmRepo.save(vm);
        if(vmDTO.getId()!=null) {
            createLog(vm, "Update");
        }else {
            createLog(vm, "Create");
        }
    }

    public void delete(Long id) {
    	createLog(findVMById(id), "Delete");
        vmRepo.deleteById(id);
    }

    public List<VMDTO> findByUserId(Long userId) {
        List<VMDTO> listOfVmDto = new ArrayList<>();
        List<VM> listOfVms = vmRepo.findAllByUserId(userId);
        for (VM vm : listOfVms) {
            listOfVmDto.add(converter.from(vm));
        }
        return listOfVmDto;
    }

    public List<VM> findAllVmByUserId(Long userId) {
        return vmRepo.findAllByUserId(userId);
    }
    public void createLog(VM vm, String operation) {
		Log appLog = new VmLogs();
		Logs log = Logs.builder().details(appLog.createLogData(vm, operation, userUtil.getCurrentUser().getUsername(), userUtil.getCurrentUserRole())).build();
		logservice.saveLogs(log);
	}
}
