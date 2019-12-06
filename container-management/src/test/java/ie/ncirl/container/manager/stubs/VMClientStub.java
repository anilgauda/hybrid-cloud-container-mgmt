package ie.ncirl.container.manager.stubs;

import ie.ncirl.container.manager.app.service.VMClient;
import ie.ncirl.container.manager.common.domain.VM;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class VMClientStub implements VMClient {

    /**
     * Gets the current available memory in a VM
     *
     * @param vm VM/server
     * @return memory in integer
     */
    public Integer getAvailableMemory(VM vm) {
        return 100;
    }

    /**
     * Installs docker service if not installed and starts it
     *
     * @param vm VM
     */
    public void configureVM(VM vm) {
    }
}
