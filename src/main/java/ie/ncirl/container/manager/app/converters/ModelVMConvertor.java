package ie.ncirl.container.manager.app.converters;

import org.springframework.stereotype.Component;

import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.model.VMModel;


@Component
public class ModelVMConvertor {
	public VM from(VMModel vmModel) {
		 return VM.builder().id(vmModel.getId()).name(vmModel.getName()).username(vmModel.getUsername())
	                .host(vmModel.getHost()).privateKey(vmModel.getPrivateKey())
	                .memory(vmModel.getMemory()).build();
    }
}
