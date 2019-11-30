package ie.ncirl.container.manager.app.converters;

import org.springframework.stereotype.Component;

import ie.ncirl.container.manager.library.configurevm.model.ApplicationModel;
import ie.ncirl.container.manager.library.configurevm.model.Container;
import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;
import ie.ncirl.container.manager.library.configurevm.model.VMModel;
import ie.ncirl.container.manager.library.deployer.dto.OptimalContainer;

@Component
public class OptimalContainerConvertor implements Converter<OptimalContainer, DeploymentModel> {

	@Override
	public OptimalContainer from(DeploymentModel domain) {

		return null;
	}

	@Override
	public DeploymentModel from(OptimalContainer dto) {
		ApplicationModel application = ApplicationModel.builder().name(dto.getContainer().getApplication().getName()).registryImageUrl(dto.getContainer().getApplication().getRegistryImageUrl()).cpu(dto.getContainer().getApplication().getCpu()).memory(dto.getContainer().getApplication().getMemory()).id(dto.getContainer().getApplication().getId()).build();
		VMModel vmUndeploy = VMModel.builder().name(dto.getContainer().getServer().getName()).username(dto.getContainer().getServer().getUsername()).host(dto.getContainer().getServer().getHost()).privateKey(dto.getContainer().getServer().getPrivateKey()).memory(dto.getContainer().getServer().getMemory()).id(dto.getContainer().getServer().getId())
				.build();
		VMModel vmDeploy = VMModel.builder().name(dto.getOptimalVM().getName()).username(dto.getOptimalVM().getUsername()).host(dto.getOptimalVM().getHost()).privateKey(dto.getOptimalVM().getPrivateKey()).memory(dto.getOptimalVM().getMemory()).id(dto.getOptimalVM().getId()).build();

		Container container = Container.builder().id(dto.getContainer().getId()).memory(dto.getContainer().getMemory()).cpu(dto.getContainer().getCpu()).application(application).server(vmUndeploy).build();
		DeploymentModel deployModel = DeploymentModel.builder().container(container).optimalVM(vmDeploy).build();
		return deployModel;
	}

}
