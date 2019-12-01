package ie.ncirl.container.manager.common.domain.validator;

import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.VM;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.Errors;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"spring.profiles.active=test"})
public class ProviderValidatorTest {

    @Autowired
    private
    ProviderValidator providerValidator;

    @Autowired
    private
    ProviderRepo providerRepo;

    @Mock
    Errors errors;

    private static int numErrors = 0;

    @Before
    public void setUp() throws Exception {
        providerRepo.save(Provider.builder().name("provider1").build());
        providerRepo.save(Provider.builder().name("provider2").build());
        providerRepo.save(Provider.builder().name("provider3").build());

        ProviderValidatorTest.numErrors = 0;
        Mockito.doAnswer(invocation -> {
            ProviderValidatorTest.numErrors++;
            return null;
        }).when(errors)
                .rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @After
    public void tearDown() throws Exception {
        providerRepo.deleteAll();
    }

    @Test
    public void supports() {
        Boolean actualRightResult = providerValidator.supports(Provider.class);
        Assert.assertEquals(true, actualRightResult);

        Boolean actualWrongResult = providerValidator.supports(VM.class);
        Assert.assertEquals(false, actualWrongResult);
    }

    @Test
    public void validateWhenNewProvider() {
        Provider provider = Provider.builder().name("newProvider").build();
        providerValidator.validate(provider, errors);

        int numErrors = ProviderValidatorTest.numErrors;
        Assert.assertEquals(numErrors, 0);
    }

    @Test
    public void validateWhenExistingProvider() {
        Provider provider = Provider.builder().name("provider1").build();
        providerValidator.validate(provider, errors);

        int numErrors = ProviderValidatorTest.numErrors;
        Assert.assertEquals(numErrors, 1);
    }

}