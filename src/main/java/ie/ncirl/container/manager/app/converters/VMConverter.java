package ie.ncirl.container.manager.app.converters;

import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.common.domain.VM;
import org.springframework.stereotype.Component;

@Component
public class VMConverter implements Converter<VMDTO, VM> {
    @Override
    public VMDTO from(VM vm) {
        return VMDTO.builder().id(vm.getId()).name(vm.getName()).build();
    }

    @Override
    public VM from(VMDTO domain) {
        return null;
    }
}
