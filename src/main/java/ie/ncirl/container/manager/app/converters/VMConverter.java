package ie.ncirl.container.manager.app.converters;

import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.common.domain.VM;
import org.springframework.stereotype.Component;

@Component
public class VMConverter implements Converter<VMDTO, VM> {
    @Override
    public VMDTO from(VM vm) {
        return VMDTO.builder().id(vm.getId()).name(vm.getName()).username(vm.getUsername())
                .host(vm.getHost()).privateKey(vm.getPrivateKey()).build();
    }

    @Override
    public VM from(VMDTO vmDTO) {
        return VM.builder().id(vmDTO.getId()).name(vmDTO.getName()).username(vmDTO.getUsername())
                .host(vmDTO.getHost()).privateKey(vmDTO.getPrivateKey()).build();
    }
}
