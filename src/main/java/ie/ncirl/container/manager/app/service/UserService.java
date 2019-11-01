package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}
