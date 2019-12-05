package ie.ncirl.container.manager.app.service;

import ie.ncirl.container.manager.app.util.CryptUtil;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.VMConfig;
import ie.ncirl.container.manager.library.configurevm.constants.VMConstants;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@Profile("!test")
public class VMClientImpl implements VMClient {

    @Autowired
    CryptUtil cryptUtil;

    /**
     * Gets the current available memory in a VM
     *
     * @param vm VM/server
     * @return memory in integer
     */
    public Integer getAvailableMemory(VM vm) {

        VMConfig config = new VMConfig();
        try {
            Map<String, Integer> stats = config.getVMStats(KeyUtils.inBytes(cryptUtil.decryptBytes(vm.getPrivateKey()))
                    , vm.getUsername(), vm.getHost());
            Integer memInKb = stats.get(VMConstants.VM_STAT_FREE_MEMORY);
            return memInKb / 1000;
        } catch (DockerException e) {
            log.error(String.format("Unable to fetch VM stats from %s", vm), e);
        }

        return 0;
    }

    /**
     * Installs docker service if not installed and starts it
     *
     * @param vm VM
     */
    public void configureVM(VM vm) {
        VMConfig config = new VMConfig();
        byte[] privateKey = KeyUtils.inBytes(cryptUtil.decryptBytes(vm.getPrivateKey()));
        try {
            log.info("VmPostListener Triggered");
            boolean isDockerInstalled = config.checkForDocker(privateKey, vm.getUsername(), vm.getHost());
            if (isDockerInstalled) {
                boolean isDockerRunning = config.checkForDockerService(privateKey, vm.getUsername(), vm.getHost());
                if (!isDockerRunning) {
                    config.startDockerService(privateKey, vm.getUsername(), vm.getHost());
                }
            } else {
                String dist = config.getLinuxDistribution(privateKey, vm.getUsername(), vm.getHost());
                config.installDocker(privateKey, vm.getUsername(), vm.getHost(), dist);
                config.startDockerService(privateKey, vm.getUsername(), vm.getHost());
            }

        } catch (DockerException e) {
            log.error("Docker Installation Failed");
        }
    }
}
