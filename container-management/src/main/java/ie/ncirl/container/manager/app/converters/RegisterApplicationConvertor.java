package ie.ncirl.container.manager.app.converters;

import org.springframework.stereotype.Component;

import ie.ncirl.container.manager.app.dto.RegisterApplicationDto;
import ie.ncirl.container.manager.common.domain.Application;

@Component
public class RegisterApplicationConvertor implements Converter<RegisterApplicationDto,Application>{

	@Override
	public RegisterApplicationDto from(Application domain) {
		RegisterApplicationDto registerApplication= RegisterApplicationDto.builder().Id(domain.getId()).cpu(domain.getCpu()).memory(domain.getMemory()).name(domain.getName()).registryImageUrl(domain.getRegistryImageUrl()).build();
		return registerApplication;
	}

	@Override
	public Application from(RegisterApplicationDto dto) {
		Application application= Application.builder().id(dto.getId()).cpu(dto.getCpu()).memory(dto.getMemory()).name(dto.getName()).registryImageUrl(dto.getRegistryImageUrl()).build();
		return application;
	}

}
