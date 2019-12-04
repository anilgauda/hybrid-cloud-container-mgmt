package ie.ncirl.container.manager.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ie.ncirl.container.manager.app.dto.UserDTO;
import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.enums.Role;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    @Autowired
    private
    UserRepo userRepo;

    @Autowired
    private
    PasswordEncoder  bCryptPasswordEncoder;

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public User save(UserDTO user) {
        return userRepo.save(User.builder()
                .username(user.getUsername())
                .password(bCryptPasswordEncoder.encode(user.getPassword()))
                .role(Role.GUEST)
                .build());
    }

    public void updateRole(Long id, String role) {
        User user = userRepo.getOne(id);
        user.setRole(Role.valueOf(role));
        userRepo.save(user);
    }

    public User findByUserId(Long id) {
        return userRepo.getOne(id);
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }
}
