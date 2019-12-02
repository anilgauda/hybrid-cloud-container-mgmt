package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.dto.UserDTO;
import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public void save(UserDTO user) {
        userRepo.save(User.builder()
                .username(user.getUsername())
                .password(bCryptPasswordEncoder.encode(user.getPassword()))
                .role(Role.GUEST)
                .build());
    }

    public User findByUserId(Long id) {
        return userRepo.getOne(id);
    }
}
