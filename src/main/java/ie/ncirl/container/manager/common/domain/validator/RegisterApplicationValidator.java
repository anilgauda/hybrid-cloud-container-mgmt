package ie.ncirl.container.manager.common.domain.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.service.ApplicationService;

@Component
public class RegisterApplicationValidator implements Validator{
	
	@Autowired
	private ApplicationService applicationService;

	@Override
	public boolean supports(Class<?> clazz) {
		return RegisterApplicationDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RegisterApplicationDto appDto=(RegisterApplicationDto)target;
	RegisterApplicationDto existingApplication = applicationService.getApplicationByName(appDto.getName());
      if (existingApplication != null) {
          errors.rejectValue("name", "not.unique", "Application with same name already exists");
      }
	}

}
