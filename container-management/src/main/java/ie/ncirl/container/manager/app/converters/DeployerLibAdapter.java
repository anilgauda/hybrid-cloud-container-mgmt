package ie.ncirl.container.manager.app.converters;

import ie.ncirl.container.manager.app.util.CryptUtil;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.deployer.dto.ApplicationData;
import ie.ncirl.container.manager.library.deployer.dto.ContainerDeploymentData;
import ie.ncirl.container.manager.library.deployer.dto.VMData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple adapter written to convert all entity classes in main project to dto classes required in library
 */
@Component
public class DeployerLibAdapter {

    @Autowired
    private
    CryptUtil cryptUtil;

    public ApplicationData fromApplication(Application application) {
        return ApplicationData.builder().name(application.getName())
                .id(application.getId())
                .registryImageUrl(application.getRegistryImageUrl())
                .cpu(application.getCpu())
                .memory(application.getMemory())
                .build();
    }

    public VMData fromVM(VM vm) {
        return VMData.builder().id(vm.getId())
                .name(vm.getName())
                .host(vm.getHost())
                .username(vm.getUsername())
                .privateKey(KeyUtils.inBytes(cryptUtil.decryptBytes(vm.getPrivateKey())))
                .memory(vm.getMemory())
                .containerDeployments(this.fromContainerDeployments(vm.getContainerDeployments()))
                .build();
    }

    public List<VMData> fromVMs(List<VM> vms) {
        return vms.stream().map(this::fromVM).collect(Collectors.toList());
    }

    private List<ContainerDeploymentData> fromContainerDeployments(List<ContainerDeployment> containerDeployments) {
        return containerDeployments.stream()
                .map(containerDeployment -> ContainerDeploymentData.builder()
                        .applicationData(this.fromApplication(containerDeployment.getApplication()))
                        .containerId(containerDeployment.getContainerId())
                        .id(containerDeployment.getId())
                        .build())
                .collect(Collectors.toList());

    }

}
