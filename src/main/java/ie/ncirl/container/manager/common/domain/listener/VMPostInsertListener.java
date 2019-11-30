package ie.ncirl.container.manager.common.domain.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.VMConfig;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;

/**This is Implementation of the Observer pattern.
 * The listener interface for receiving VMPostInsert events.
 * The class that is interested in processing a VMPostInsert
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addVMPostInsertListener<code> method. When
 * the VMPostInsert event occurs, that object's appropriate
 * method is invoked.
 *
 * @see VMPostInsertEvent
 */
public class VMPostInsertListener {

	/** The logger. */
	Logger logger=Logger.getLogger(VMPostInsertListener.class.getName());
	
	/**
	 * Configure VM.
	 *
	 * @param vm the vm
	 */
	@PostUpdate
	@PostPersist
	public void configureVM(VM vm) {
		VMConfig config=new VMConfig();
		try {
			logger.log(Level.INFO,"VmPostListener Triggered");
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
			logger.log(Level.SEVERE,"Docker Installation Failed");
		}
	}
}
