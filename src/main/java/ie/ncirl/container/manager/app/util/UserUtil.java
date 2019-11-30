package ie.ncirl.container.manager.app.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ie.ncirl.container.manager.app.repository.UserRepo;
import ie.ncirl.container.manager.common.domain.User;

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
		return userRepo.findByUsername(username);
	}
}
