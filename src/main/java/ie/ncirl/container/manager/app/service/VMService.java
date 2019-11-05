package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.VMRepo;
import ie.ncirl.container.manager.common.domain.VM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VMService {
    @Autowired
    private
    VMRepo vmRepo;

    @Autowired
    private
    VMConverter converter;

    public List<VMDTO> getAllVMs() {
        return converter.fromDomainList(vmRepo.findAll());
    }

    public VMDTO findByName(String name) {
        return converter.from(vmRepo.findByName(name));
    }

    public VMDTO findById(Long id) {
        return converter.from(vmRepo.getOne(id));
    }

    public void save(VMDTO vmDTO) {
        vmRepo.save(converter.from(vmDTO));
    }

    public void delete(Long id) {
        vmRepo.deleteById(id);
    }
}
