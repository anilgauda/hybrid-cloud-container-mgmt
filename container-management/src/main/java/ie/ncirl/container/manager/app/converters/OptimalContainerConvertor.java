package ie.ncirl.container.manager.app.converters;

import ie.ncirl.container.manager.library.configurevm.model.ApplicationModel;
import ie.ncirl.container.manager.library.configurevm.model.Container;
import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;
import ie.ncirl.container.manager.library.configurevm.model.VMModel;
import ie.ncirl.container.manager.library.deployer.dto.OptimalContainer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OptimalContainerConvertor {

    public List<DeploymentModel> fromOptimalContainers(List<OptimalContainer> optimalContainers) {
        return optimalContainers.stream().map(this::from).collect(Collectors.toList());
    }

    private DeploymentModel from(OptimalContainer dto) {
        ApplicationModel application = ApplicationModel.builder()
                .name(dto.getContainer().getApplicationData().getName())
                .registryImageUrl(dto.getContainer().getApplicationData().getRegistryImageUrl())
                .cpu(dto.getContainer().getApplicationData().getCpu())
                .memory(dto.getContainer().getApplicationData().getMemory())
                .id(dto.getContainer().getApplicationData().getId()).build();

        VMModel vmUndeploy = VMModel.builder()
                .name(dto.getContainer().getServer().getName())
                .username(dto.getContainer().getServer().getUsername())
                .host(dto.getContainer().getServer().getHost())
                .privateKey(dto.getContainer().getServer().getPrivateKey())
                .memory(dto.getContainer().getServer().getMemory())
                .id(dto.getContainer().getServer().getId())
                .build();

        VMModel vmDeploy = VMModel.builder()
                .name(dto.getOptimalVMData().getName())
                .username(dto.getOptimalVMData().getUsername())
                .host(dto.getOptimalVMData().getHost())
                .privateKey(dto.getOptimalVMData().getPrivateKey())
                .memory(dto.getOptimalVMData().getMemory())
                .id(dto.getOptimalVMData().getId()).build();

        Container container = Container.builder()
                .id(dto.getContainer().getId())
                .memory(dto.getContainer().getMemory())
                .cpu(dto.getContainer().getCpu())
                .application(application)
                .server(vmUndeploy).build();

        return DeploymentModel.builder().container(container).optimalVM(vmDeploy).build();
    }

}
