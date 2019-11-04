package ie.ncirl.container.manager.library.deployer.util;

import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.VMConfig;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DockerManagerUtil {

    /*public static List<String> getContainerIdsInVm(VM vm) {
        //vm = VM.builder().host("localhost").build(); // TODO: Test ?

        ConfigureVM config = new ConfigureVM();
        List<String> containerIds = new ArrayList<>();
        try {
            containerIds = config.getContainerIds(vm.getKeyFileName(), vm.getUsername(), vm.getHost());
        } catch (DockerInstallationException exception) {
            log.error(String.format("Unable to get container ids for VM: %s", vm.getHost()), exception);
        }

        return containerIds;
    }*/
}
