package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.dto.UserDTO;
import ie.ncirl.container.manager.common.domain.enums.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(value={"spring.profiles.active=test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTest {

    @Autowired
    private
    UserRepo userRepo;

    @Autowired
    private
    UserService userService;

    @Mock
    BCryptPasswordEncoder encoder;

    @Before
    public void setUp() {
    	 User user = User.builder().username("adesh").email("adesh@gmail.com").password("pass").build();
         userRepo.save(user);

        ReflectionTestUtils.setField(userService, "bCryptPasswordEncoder", encoder);
        Mockito.when(encoder.encode(Mockito.any())).thenReturn("###");
    }


    @After
    public void cleanUp() {
        userRepo.deleteAll();
    }

    @Test
    public void testFindByUsername() {
        User userFound = userService.findByUsername("adesh");
        assertEquals("adesh", userFound.getUsername());
    }

    @Test
    public void save() {
        User notExistingUser = userService.findByUsername("user");
        assertNull(notExistingUser);

        userService.save(UserDTO.builder().username("user").build());

        User user = userService.findByUsername("user");
        assertNotNull(user);
    }

    @Test
    public void updateRole() {
        User user = userService.findByUsername("adesh");
        assertEquals(Role.GUEST, user.getRole());

        userService.updateRole(user.getId(), Role.USER.name());

        User updatedUser = userService.findByUsername("adesh");
        assertEquals(Role.USER, updatedUser.getRole());
    }

    @Test
    public void findByUserId() {
        User user = userService.save(UserDTO.builder().username("anil").build());

        User userFound = userService.findByUserId(user.getId());
        assertEquals(user, userFound);
    }

    @Test
    public void findAll() {
        List<User> users = userService.findAll();

        assertThat(users.size(), greaterThan(0));
    }
}