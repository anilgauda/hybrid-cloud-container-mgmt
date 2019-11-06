package ie.ncirl.container.manager.app.util;


import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class will be refactored after user authentication has been implemented
 */
@Component
public class UserUtil {

    @Autowired
    private
    UserRepo userRepo;

    public User getCurrentUser() {
        return userRepo.findAll().get(0);
    }
}
