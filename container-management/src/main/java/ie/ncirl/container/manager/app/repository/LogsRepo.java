package ie.ncirl.container.manager.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ie.ncirl.container.manager.common.domain.Logs;

@Repository
public interface LogsRepo extends JpaRepository<Logs, Long> {

}
