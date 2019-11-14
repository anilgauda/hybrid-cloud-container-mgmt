package ie.ncirl.container.manager.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ie.ncirl.container.manager.common.domain.VM;

@Repository
public interface VMRepo extends JpaRepository<VM, Long>{

    public VM findByName(String name);
    public List<VM> findAllByUserId(Long userId);
}
