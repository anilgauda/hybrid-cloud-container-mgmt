package ie.ncirl.container.manager.app.converters;

import org.springframework.stereotype.Component;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.library.configurevm.model.ApplicationModel;


@Component
public class ModelAppConvertor {
	public Application from(ApplicationModel appModel) {
		return  Application.builder().id(appModel.getId()).cpu(appModel.getCpu()).memory(appModel.getMemory()).name(appModel.getName()).registryImageUrl(appModel.getRegistryImageUrl()).build();
		
	}
}
