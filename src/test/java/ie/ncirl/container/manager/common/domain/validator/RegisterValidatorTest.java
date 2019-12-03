package ie.ncirl.container.manager.common.domain.validator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.Errors;

import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.app.repository.ApplicationRepo;
import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.enums.Role;

@RunWith(SpringRunner.class)
@SpringBootTest(value = { "spring.profiles.active=test" })
public class RegisterValidatorTest {

	@Autowired
	RegisterApplicationValidator applicationValidator;

	@Autowired
	ApplicationRepo applicationRepo;

	@Autowired
	UserRepo userRepo;

	@Mock
	Errors errors;

	private static int numErrors = 0;

	@Before
	public void setUp() {
		User user = User.builder().username("admin").role(Role.USER).build();
		userRepo.save(user);
		applicationRepo.save(Application.builder().name("App1").user(user).build());
		applicationRepo.save(Application.builder().name("App2").user(user).build());
		applicationRepo.save(Application.builder().name("App3").user(user).build());

		RegisterValidatorTest.numErrors = 0;
		Mockito.doAnswer(invocation -> {
			RegisterValidatorTest.numErrors++;
			return null;
		}).when(errors).rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

	}

	@Test
	public void testSupports() {
		Boolean rightResult = applicationValidator.supports(RegisterApplicationDto.class);
		Assert.assertEquals(true, rightResult);

		Boolean wrongResult = applicationValidator.supports(ContainerDeployment.class);
		Assert.assertEquals(false, wrongResult);
	}

	@Test
	public void validateWhenNewApp() {
		RegisterApplicationDto provider = RegisterApplicationDto.builder().name("newApp").build();
		applicationValidator.validate(provider, errors);

		int numErrors = RegisterValidatorTest.numErrors;
		Assert.assertEquals(numErrors, 0);
	}

	@Test
	public void validateWhenExistingApp() {
		RegisterApplicationDto provider = RegisterApplicationDto.builder().name("App1").build();
		applicationValidator.validate(provider, errors);
		int numErrors = RegisterValidatorTest.numErrors;
		Assert.assertEquals(numErrors, 1);
	}

	@After
	public void cleanUp() throws Exception {
		applicationRepo.deleteAll();
		userRepo.deleteAll();
	}

}
