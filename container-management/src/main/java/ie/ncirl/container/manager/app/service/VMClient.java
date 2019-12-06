package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.VMConfig;
import ie.ncirl.container.manager.library.configurevm.constants.VMConstants;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface VMClient {

    /**
     * Gets the current available memory in a VM
     *
     * @param vm VM/server
     * @return memory in integer
     */
    public Integer getAvailableMemory(VM vm);

    /**
     * Installs docker service if not installed and starts it
     *
     * @param vm VM
     */
    public void configureVM(VM vm);
}
