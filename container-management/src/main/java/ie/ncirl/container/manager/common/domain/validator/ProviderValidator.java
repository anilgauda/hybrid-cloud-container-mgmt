package ie.ncirl.container.manager.common.domain.validator;

import ie.ncirl.container.manager.app.service.ProviderService;
import ie.ncirl.container.manager.common.domain.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProviderValidator implements Validator {

    @Autowired
    private
    ProviderService providerService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Provider.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Provider provider = (Provider) target;
        Provider existingProvider = providerService.findByName(provider.getName());
        if (existingProvider != null && !existingProvider.equals(provider)) {
            errors.rejectValue("name", "not.unique", "Provider with same name already exists");
        }
    }
}

