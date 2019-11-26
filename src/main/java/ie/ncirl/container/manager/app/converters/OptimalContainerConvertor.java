package ie.ncirl.container.manager.app.converters;

import org.springframework.stereotype.Component;

import ie.ncirl.container.manager.library.configurevm.model.Application;
import ie.ncirl.container.manager.library.configurevm.model.Container;
import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;
import ie.ncirl.container.manager.library.configurevm.model.VM;
import ie.ncirl.container.manager.library.deployer.dto.OptimalContainer;

@Component
public class OptimalContainerConvertor implements Converter<OptimalContainer, DeploymentModel> {

	@Override
	public OptimalContainer from(DeploymentModel domain) {

		return null;
	}

	@Override
	public DeploymentModel from(OptimalContainer dto) {
		Application application = Application.builder().name(dto.getContainer().getApplication().getName()).registryImageUrl(dto.getContainer().getApplication().getRegistryImageUrl()).build();
		VM vmUndeploy = VM.builder().name(dto.getContainer().getServer().getName()).username(dto.getContainer().getServer().getUsername()).host(dto.getContainer().getServer().getHost()).privateKey(dto.getContainer().getServer().getPrivateKey())
				.build();
		VM vmDeploy = VM.builder().name(dto.getOptimalVM().getName()).username(dto.getOptimalVM().getUsername()).host(dto.getOptimalVM().getHost()).privateKey(dto.getOptimalVM().getPrivateKey()).build();

		Container container = Container.builder().application(application).server(vmUndeploy).build();
		DeploymentModel deployModel = DeploymentModel.builder().container(container).optimalVM(vmDeploy).build();
		return deployModel;
	}

}
