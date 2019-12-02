package ie.ncirl.container.manager.app.util;

import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;
import ie.ncirl.container.manager.common.domain.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * This class will be refactored after user authentication has been implemented
 */
@Component
public class UserUtil {

    @Autowired
    private UserRepo userRepo;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        System.out.println("Current role :" + auth.getAuthorities().toString());
        return userRepo.findByUsername(username);
    }

    public String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        System.out.println("Current role :" + auth.getAuthorities());
        return auth.getAuthorities().toString();
    }

    /**
     * Returns true if current user can modify role
     *
     * @return boolean
     */
    public boolean canModifyRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals(Role.USER.name()));
    }
}
