package ie.ncirl.container.manager.app.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ie.ncirl.container.manager.common.domain.ContainerDeployment;

@Repository
public interface ContainerDeploymentRepo extends JpaRepository<ContainerDeployment, Long>{
	
	public List<ContainerDeployment> findAllByApplicationId(Long appId);
	
	@Transactional
	public void deleteByApplicationId(Long appId);
	
}
