package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.dto.VMDTO;
import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.library.configurevm.VMConfigureTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"spring.profiles.active=dev"})
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
    ProviderRepo providerRepo;

    @Test
    public void save() {
        VMDTO vmDto = VMDTO.builder().host(VMConfigureTest.IP_ADDRESS)
                .name("My VM")
                .privateKey(VMConfigureTest.privateKey)
                .username(VMConfigureTest.USERNAME)
                .providerId(providerRepo.findAll().get(0).getId())
                .build();

        vmService.save(vmDto);
    }
}