package ie.ncirl.container.manager;

import ie.ncirl.container.manager.library.configurevm.VMConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
@ComponentScan("ie.ncirl.container.manager")
public class CmSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CmSystemApplication.class, args);
	}

	@Bean
	public VMConfig getVMConfig() {
		return new VMConfig();
	}
}
