package ie.ncirl.container.manager.app.converters;

import org.springframework.stereotype.Component;

import ie.ncirl.container.manager.app.dto.LogsDto;
import ie.ncirl.container.manager.common.domain.Logs;

@Component
public class LogsConvertor implements Converter<LogsDto, Logs> {

	@Override
	public LogsDto from(Logs domain) {
		return LogsDto.builder().details(domain.getDetails()).build();
	}

	@Override
	public Logs from(LogsDto dto) {
		return Logs.builder().details(dto.getDetails()).build();
	}

}
