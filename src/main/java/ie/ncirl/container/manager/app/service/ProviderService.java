package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.repository.ProviderRepo;
import ie.ncirl.container.manager.common.domain.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProviderService {

    @Autowired
    private
    ProviderRepo providerRepo;

    public List<Provider> getAllProviders() {
        return providerRepo.findAll();
    }

    public Provider findByName(String name) {
        return providerRepo.findByName(name);
    }

    public void save(Provider provider) {
        providerRepo.save(provider);
    }
}
