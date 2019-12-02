package ie.ncirl.container.manager.common.domain.validator;

import ie.ncirl.container.manager.app.dto.UserDTO;
import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.common.domain.enums.Role;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"spring.profiles.active=test"})
public class UserValidatorTest {

    @Autowired
    private
    UserValidator userValidator;

    @Autowired
    private
    UserRepo userRepo;

    @Mock
    Errors errors;

    private static int numErrors = 0;

    @Before
    public void setUp() throws Exception {
        userRepo.save(User.builder().username("user1").role(Role.USER).build());
        userRepo.save(User.builder().username("user2").role(Role.USER).build());

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("admin");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserValidatorTest.numErrors = 0;
        Mockito.doAnswer(invocation -> {
            UserValidatorTest.numErrors++;
            return null;
        }).when(errors)
                .rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @After
    public void tearDown() throws Exception {
        userRepo.deleteAll();
    }


    @Test
    public void supports() {
        Boolean actualRightResult = userValidator.supports(UserDTO.class);
        Assert.assertEquals(true, actualRightResult);

        Boolean actualWrongResult = userValidator.supports(User.class);
        Assert.assertEquals(false, actualWrongResult);
    }

    @Test
    public void validateWhenPasswordsDontMatch() {
        UserDTO userDTO = UserDTO.builder().password("1234").confirmPassword("12345").build();
        userValidator.validate(userDTO, errors);

        Assert.assertEquals(UserValidatorTest.numErrors, 1);
    }

    @Test
    public void validateWhenUserDoesNotExist() {
        UserDTO userDTO = UserDTO.builder().username("newUser").password("1234").confirmPassword("1234").build();
        userValidator.validate(userDTO, errors);

        Assert.assertEquals(UserValidatorTest.numErrors, 0);
    }

    @Test
    public void validateWhenUsernameAlreadyExist() {
        UserDTO userDTO = UserDTO.builder().username("user1").password("1234").confirmPassword("1234").build();
        userValidator.validate(userDTO, errors);

        Assert.assertEquals(UserValidatorTest.numErrors, 1);
    }

}