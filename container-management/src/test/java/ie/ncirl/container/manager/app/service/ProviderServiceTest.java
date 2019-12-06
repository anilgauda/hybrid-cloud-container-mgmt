package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.common.domain.Provider;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.enums.Role;
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
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.greaterThan;

@RunWith(SpringRunner.class)
@SpringBootTest(value = {"spring.profiles.active=test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProviderServiceTest {

    @Autowired
    private
    ProviderService providerService;

    @Autowired
    private
    ProviderRepo providerRepo;

    @Before
    public void setUp() throws Exception {
        providerRepo.save(Provider.builder().name("provider1").build());
        providerRepo.save(Provider.builder().name("provider2").build());
        providerRepo.save(Provider.builder().name("provider3").build());
    }

    @After
    public void tearDown() throws Exception {
        providerRepo.deleteAll();
    }


    @Test
    public void getAllProviders() {
        List<Provider> providers = providerService.getAllProviders();
        Assert.assertThat(providers.size(), greaterThan(2));
    }

    @Test
    @Transactional
    public void findById() {
        Provider expectedProvider = providerService.findByName("provider1");
        Provider actualProvider = providerService.findById(expectedProvider.getId());

        Assert.assertEquals(expectedProvider, actualProvider);
    }

    @Test
    public void findByName() {
        Provider provider = providerService.findByName("provider1");
        Assert.assertNotNull(provider);
    }

    @Test
    public void delete() {
        Provider provider = providerService.findByName("provider1");
        Assert.assertNotNull(provider);

        providerService.delete(provider.getId());

        Provider deletedProvider = providerService.findByName("provider1");
        Assert.assertNull(deletedProvider);
    }

    @Test
    public void save() {
        String providerName = "someProvider";
        Provider notExistingProvider = providerService.findByName(providerName);
        Assert.assertNull(notExistingProvider);

        providerService.save(Provider.builder().name(providerName).build());

        Provider savedProvider = providerService.findByName(providerName);
        Assert.assertNotNull(savedProvider);
    }
}