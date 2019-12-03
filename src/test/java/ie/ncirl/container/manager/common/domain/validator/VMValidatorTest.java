package ie.ncirl.container.manager.common.domain.validator;

import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.app.repository.VMRepo;
import ie.ncirl.container.manager.app.service.VMService;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.common.domain.enums.Role;
import ie.ncirl.container.manager.library.configurevm.VMConfig;
import ie.ncirl.container.manager.library.configurevm.VMConfigureTest;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Errors;

@RunWith(SpringRunner.class)
@SpringBootTest(value = { "spring.profiles.active=test" })
public class VMValidatorTest {

    @Autowired
    private
    VMValidator vmValidator;

    @Autowired
    private
    VMService vmService;

    @Autowired
    private
    UserRepo userRepo;

    @Autowired
    private
    VMRepo vmRepo;

    @Autowired
    private
    ProviderRepo providerRepo;

    @Mock
    Errors errors;

    @Mock
    VMConfig vmConfig;

    private static int numErrors = 0;

    @Before
    public void setUp() throws Exception {
        userRepo.save(User.builder().username("admin").role(Role.USER).build());

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("admin");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        providerRepo.save(Provider.builder().name("adminProvider").build());
        vmService.save(buildVMDTO("adminVM1"));
        vmService.save(buildVMDTO("adminVM2"));
        vmService.save(buildVMDTO("adminVM3"));

        VMValidatorTest.numErrors = 0;
        Mockito.doAnswer(invocation -> {
            VMValidatorTest.numErrors++;
            return null;
        }).when(errors)
                .rejectValue(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        Mockito.when(vmConfig.getLinuxDistribution(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("Ubuntu");
    }

    @After
    public void tearDown() throws Exception {
        vmRepo.deleteAll();
        providerRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    public void supports() {
        Boolean actualRightResult = vmValidator.supports(VMDTO.class);
        Assert.assertEquals(true, actualRightResult);

        Boolean actualWrongResult = vmValidator.supports(VM.class);
        Assert.assertEquals(false, actualWrongResult);
    }

    @Test
    public void validateWithNewVM() {
        VMDTO newVM = buildVMDTO("newVM");

        ReflectionTestUtils.setField(vmValidator, "vmConfig", vmConfig);
        vmValidator.validate(newVM, errors);

        int numErrors = VMValidatorTest.numErrors;
        Assert.assertEquals(numErrors, 0);
    }

    @Test
    public void validateWithExistingVM() {
        VMDTO existingVM = vmService.findByName("adminVM1");

        ReflectionTestUtils.setField(vmValidator, "vmConfig", vmConfig);
        vmValidator.validate(existingVM, errors);

        int numErrors = VMValidatorTest.numErrors;
        Assert.assertEquals(numErrors, 0);
    }

    /**
     * A common method to build a test vm object
     *
     * @param vmName Name of VM
     * @return VMDTO object
     */
    private VMDTO buildVMDTO(String vmName) {
        return VMDTO.builder().host(VMConfigureTest.IP_ADDRESS)
                .name(vmName)
                .privateKey(KeyUtils.inString(VMConfigureTest.privateKey))
                .username(VMConfigureTest.USERNAME)
                .providerId(providerRepo.findAll().get(0).getId())
                .build();

    }
}