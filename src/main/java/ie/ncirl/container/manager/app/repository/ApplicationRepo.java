package ie.ncirl.container.manager.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ie.ncirl.container.manager.common.domain.Application;

@Repository
public interface ApplicationRepo extends JpaRepository<Application, Long>{

	public Application findByName(String name);
	
	@Query("From Application a where a.user.id= :userId")
	public List<Application> getAllApplicationByUserId(@Param("userId") Long userId);
	
	
}
