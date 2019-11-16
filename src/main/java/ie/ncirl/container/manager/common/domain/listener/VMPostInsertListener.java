package ie.ncirl.container.manager.common.domain.listener;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.VMConfig;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;

public class VMPostInsertListener {

	@PostUpdate
	@PostPersist
	public void configureVM(VM vm) {
		VMConfig config=new VMConfig();
		try {
			System.out.println("Event Triggered");
			boolean isDockerInstalled=config.checkForDocker(vm.getPrivateKey(), vm.getUsername(), vm.getHost());
			if(isDockerInstalled) {
				boolean isDockerRunning=config.checkForDockerService(vm.getPrivateKey(), vm.getUsername(), vm.getHost());
				if(!isDockerRunning) {
					config.startDockerService(vm.getPrivateKey(), vm.getUsername(), vm.getHost());
				}
			}else {
				String dist=config.getLinuxDistribution(vm.getPrivateKey(), vm.getUsername(), vm.getHost());
				config.installDocker(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), dist);
				config.startDockerService(vm.getPrivateKey(), vm.getUsername(), vm.getHost());
			}
			
		} catch (DockerException e) {
			System.out.println("Docker Instllation Failed");
		}
	}
}
