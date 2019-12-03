package ie.ncirl.container.manager.app.service;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;

@RunWith(SpringRunner.class)
@SpringBootTest(value={"spring.profiles.active=test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    UserRepo userRepo;
    
    @Before
    public void setUp() {
    	 User user = User.builder().username("adesh").email("adesh@gmail.com").password("###").build();
         userRepo.save(user);
    }

    @Test
    public void testFindByUsername() {
        User userFound = userRepo.findByUsername("adesh");
        assertEquals("adesh", userFound.getUsername());
    }
    @After
    public void cleanUp() {
    	userRepo.deleteAll();
    }
}