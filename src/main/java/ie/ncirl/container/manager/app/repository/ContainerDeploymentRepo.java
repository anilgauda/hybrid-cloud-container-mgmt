package ie.ncirl.container.manager.app.repository;

import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ContainerDeploymentRepo extends JpaRepository<ContainerDeployment, Long> {

    List<ContainerDeployment> findAllByApplicationId(Long appId);

    @Transactional
    void deleteByApplicationId(Long appId);

    @Transactional
    void deleteByContainerId(String containerId);
}
