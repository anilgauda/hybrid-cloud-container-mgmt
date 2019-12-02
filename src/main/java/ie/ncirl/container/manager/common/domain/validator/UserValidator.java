package ie.ncirl.container.manager.common.domain.validator;

import ie.ncirl.container.manager.app.dto.UserDTO;
import ie.ncirl.container.manager.app.service.UserService;
import ie.ncirl.container.manager.common.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    @Autowired
    private
    UserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO user = (UserDTO) target;

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("password", "not.same", "Passwords do not match");
        }

        User existingUser = userService.findByUsername(user.getUsername());
        if (existingUser != null) {
            errors.rejectValue("username", "not.unique", "User with same username already exists");
        }
    }
}

