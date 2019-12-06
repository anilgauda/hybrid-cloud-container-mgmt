package ie.ncirl.container.manager.app.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.enums.Role;
import junit.framework.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(value = { "spring.profiles.active=test" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SimpleUserDetailsServiceTests {

    @Autowired
    private UserRepo userRepository;
    
    @Autowired
    private SimpleUserDetailsService userService;
    
    @Before
    public void setUp() {
    	User user=User.builder().username("admin").password("admin").role(Role.USER).build();
    	userRepository.save(user);
    }
    
    @Test
    public void testLoadUserByUsername() {
    	UserDetails userDetails=userService.loadUserByUsername("admin");
    	Assert.assertEquals("admin", userDetails.getUsername());
    }
    @After
    public void cleanUp() {
    	userRepository.deleteAll();
    }
}
