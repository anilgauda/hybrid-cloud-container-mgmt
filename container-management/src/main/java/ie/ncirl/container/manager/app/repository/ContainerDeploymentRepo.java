package ie.ncirl.container.manager.app.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ie.ncirl.container.manager.common.domain.ContainerDeployment;

@Repository
public interface ContainerDeploymentRepo extends JpaRepository<ContainerDeployment, Long> {

    List<ContainerDeployment> findAllByApplicationId(Long appId);

    @Transactional
    void deleteByApplicationId(Long appId);

    @Transactional
    void deleteByContainerId(String containerId);

	Page<ContainerDeployment> findAllByApplicationId(Long appId, Pageable pageable);

}
