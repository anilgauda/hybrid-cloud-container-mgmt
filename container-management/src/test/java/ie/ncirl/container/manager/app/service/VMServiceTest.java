package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.converters.VMConverter;
import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.app.repository.VMRepo;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.app.util.UserUtil;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.common.domain.enums.Role;
import ie.ncirl.container.manager.stubs.VMConfigStub;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"spring.profiles.active=test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class VMServiceTest {

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
    VMConverter converter;

    @Autowired
    private
    UserUtil userUtil;

    @Autowired
    private
    ProviderRepo providerRepo;

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
    }

    @After
    public void tearDown() throws Exception {
        vmRepo.deleteAll();
        providerRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    public void save() {
        String vmName = "My VM";
        VMDTO vmDto = VMDTO.builder().host(VMConfigStub.IP_ADDRESS)
                .name(vmName)
                .privateKey(KeyUtils.inString(VMConfigStub.privateKey))
                .username(VMConfigStub.USERNAME)
                .providerId(providerRepo.findAll().get(0).getId())
                .build();

        vmService.save(vmDto);

        VMDTO vm = vmService.findByName(vmName);
        Assert.assertNotNull(vm);
    }

    @Test
    public void getAllVMs() {
        List<VMDTO> vms = vmService.getAllVMs();

        Assert.assertThat(vms.size(), greaterThan(2));
    }

    @Test
    public void findByVmIds() {
        String vmName = "VM1";
        vmService.save(buildVMDTO(vmName));

        VMDTO vm = vmService.findByName(vmName);

        List<String> vmIds = new ArrayList<>();
        vmIds.add(String.valueOf(vm.getId()));

        List<VM> vms = vmService.findByVmIds(vmIds);

        Assert.assertEquals(vms.size(), 1);
    }

    @Test
    public void findByName() {
        String vmName = "VM1";
        vmService.save(buildVMDTO(vmName));

        VMDTO vm = vmService.findByName(vmName);

        Assert.assertNotNull(vm);
    }

    @Test
    @Transactional
    public void findVMById() {
        String vmName = "VM1";
        vmService.save(buildVMDTO(vmName));

        VMDTO expectedVm = vmService.findByName(vmName);
        VM vm = vmService.findVMById(expectedVm.getId());
        VMDTO actualVm = converter.from(vm);

        Assert.assertEquals(expectedVm, actualVm);
    }

    @Test
    @Transactional
    public void findById() {
        String vmName = "VM1";
        vmService.save(buildVMDTO(vmName));

        VMDTO expectedVm = vmService.findByName(vmName);
        VMDTO actualVm = vmService.findById(expectedVm.getId());

        Assert.assertEquals(expectedVm, actualVm);
    }


    @Test
    public void delete() {
        String vmName = "VM1";
        vmService.save(buildVMDTO(vmName));

        VMDTO vm = vmService.findByName(vmName);
        Assert.assertNotNull(vm);

        vmService.delete(vm.getId());

        VMDTO deletedVm = vmService.findByName(vmName);
        Assert.assertNull(deletedVm);
    }

    @Test
    public void findByUserId() {
        User currentUser = userUtil.getCurrentUser();
        List<VMDTO> vms = vmService.findByUserId(currentUser.getId());

        Assert.assertThat(vms.size(), greaterThan(1));
    }

    @Test
    public void findAllVmByUserId() {
        User currentUser = userUtil.getCurrentUser();
        List<VM> vms = vmService.findAllVmByUserId(currentUser.getId());

        Assert.assertThat(vms.size(), greaterThan(1));
    }

    /**
     * A common method to build a test vm object
     *
     * @param vmName Name of VM
     * @return VMDTO object
     */
    private VMDTO buildVMDTO(String vmName) {
        return VMDTO.builder().host(VMConfigStub.IP_ADDRESS)
                .name(vmName)
                .privateKey(KeyUtils.inString(VMConfigStub.privateKey))
                .username(VMConfigStub.USERNAME)
                .providerId(providerRepo.findAll().get(0).getId())
                .build();

    }

    @Test
    public void findAllVMs() {
        List<VM> vms = vmService.findAllVMs();

        Assert.assertThat(vms.size(), greaterThan(2));
    }
}