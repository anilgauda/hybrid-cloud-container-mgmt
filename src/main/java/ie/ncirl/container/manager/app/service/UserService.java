package ie.ncirl.container.manager.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
    
    public User findByUserId(Long id) {
    	return userRepo.getOne(id);
    }
}
