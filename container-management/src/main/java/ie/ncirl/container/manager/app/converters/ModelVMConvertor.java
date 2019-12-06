package ie.ncirl.container.manager.app.converters;

import ie.ncirl.container.manager.app.util.CryptUtil;
import ie.ncirl.container.manager.app.util.KeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.model.VMModel;


@Component
public class ModelVMConvertor {

	@Autowired
	CryptUtil cryptUtil;

	public VM from(VMModel vmModel) {
		 return VM.builder().id(vmModel.getId()).name(vmModel.getName()).username(vmModel.getUsername())
	                .host(vmModel.getHost()).privateKey(KeyUtils.inBytes(cryptUtil.encryptBytes(vmModel.getPrivateKey())))
	                .memory(vmModel.getMemory()).build();
    }
}
