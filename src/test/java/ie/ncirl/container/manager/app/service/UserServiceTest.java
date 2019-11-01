package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(value={"spring.profiles.active=dev"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    UserRepo userRepo;

    @Test
    public void testFindByUsername() {
        User user = User.builder().username("adesh").email("adesh@gmail.com").password("###").build();
        userRepo.save(user);

        User userFound = userRepo.findByUsername("adesh");

        assertEquals(user.getUsername(), userFound.getUsername());
    }
}