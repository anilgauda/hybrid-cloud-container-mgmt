package ie.ncirl.container.manager.app.repository;

import ie.ncirl.container.manager.common.domain.VM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VMRepo extends JpaRepository<VM, Long> {
    VM findByName(String name);

    List<VM> findAllByUserId(Long userId);

    List<VM> findByIdIn(List<Long> ids);
}
