package ie.ncirl.container.manager.common.domain.validator;

import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.service.ProviderService;
import ie.ncirl.container.manager.app.service.VMService;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.VMConfig;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class VMValidator implements Validator {

    @Autowired
    private
    VMService vmService;

    @Override
    public boolean supports(Class<?> clazz) {
        return VMDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        VMDTO vmDTO = (VMDTO) target;
        VMDTO existingVM = vmService.findByName(vmDTO.getName());
        if (existingVM != null && !existingVM.getId().equals(vmDTO.getId())) {
            errors.rejectValue("name", "not.unique", "Server with same name already exists");
        }

        validateVMAccessDetails(vmDTO, errors);
    }

    private void validateVMAccessDetails(VMDTO vmdto, Errors errors) {
        VMConfig config = new VMConfig();
        try {
            config.getLinuxDistribution(KeyUtils.inBytes(vmdto.getPrivateKey()), vmdto.getUsername(), vmdto.getHost());
        } catch (DockerException e) {
            errors.rejectValue("privateKey", "invalid", " username/private key access details are invalid");
        }
    }
}

