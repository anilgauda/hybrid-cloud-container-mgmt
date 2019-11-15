package ie.ncirl.container.manager.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.app.repository.VMRepo;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.VMConfig;
import ie.ncirl.container.manager.library.configurevm.constants.VMConstants;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VMService {
    @Autowired
    private
    VMRepo vmRepo;

    @Autowired
    ProviderRepo providerRepo;

    @Autowired
    private
    VMConverter converter;

    @Autowired
    private
    UserUtil userUtil;

    public List<VMDTO> getAllVMs() {
        return converter.fromDomainList(vmRepo.findAll());
    }

    public VMDTO findByName(String name) {
        return converter.from(vmRepo.findByName(name));
    }

    public VMDTO findById(Long id) {
        VM vm = vmRepo.getOne(id);
        VMDTO vmdto = converter.from(vmRepo.getOne(id));
        vmdto.setProviderId(vm.getProvider().getId());
        return vmdto;
    }

    public void save(VMDTO vmDTO) {
        VM vm = converter.from(vmDTO);
        vm.setUser(userUtil.getCurrentUser());
        vm.setProvider(providerRepo.findById(vmDTO.getProviderId()).get());

        if (vmDTO.getId() == null) {
            vm.setMemory(getAvailableMemory(vm));
        }

        vmRepo.save(vm);
    }

    public void delete(Long id) {
        vmRepo.deleteById(id);
    }

    /**
     * Gets the current available memory in a VM
     *
     * @param vm VM/server
     * @return memory in integer
     */
    private Integer getAvailableMemory(VM vm) {

        VMConfig config = new VMConfig();
        try {
            Map<String, Integer> stats = config.getVMStats(vm.getPrivateKey(), vm.getUsername(), vm.getHost());
            Integer memInKb = stats.get(VMConstants.VM_STAT_FREE_MEMORY);
            return memInKb / 1000;
        } catch (DockerException e) {
            log.error(String.format("Unable to fetch VM stats from %s", vm), e);
        }

        return 0;
    }

    public List<VMDTO> findByUserId(Long userId){
    	List<VMDTO> listOfVmDto=new ArrayList<>();
    	List<VM> listOfVms=vmRepo.findAllByUserId(userId);
    	for(VM vm:listOfVms) {
    		listOfVmDto.add(converter.from(vm));
    	}
		return listOfVmDto;
    }
}
