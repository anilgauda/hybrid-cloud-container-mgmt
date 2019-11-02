package ie.ncirl.container.manager.app.repository;

import ie.ncirl.container.manager.common.domain.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepo extends JpaRepository<Provider, Long> {
}
