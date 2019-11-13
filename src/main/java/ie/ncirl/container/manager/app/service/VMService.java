package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.app.repository.VMRepo;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.common.domain.VM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        vmRepo.save(vm);
    }

    public void delete(Long id) {
        vmRepo.deleteById(id);
    }

    /**
     * Gets the current available memory in a VM
     * @param vm VM/server
     * @return memory in integer
     */
    public Integer getAvailableMemory(VM vm) {
        return 0;
    }

}
