package ie.ncirl.container.manager.library.configurevm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.JSchException;

import ie.ncirl.container.manager.library.configurevm.constants.VMConstants;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;


public class VMConfig {
	
	Logger logger= Logger.getLogger(VMConfig.class.getName());
	/** The connection. */
	VMConnection connection=VMConnection.getConnection();

	/**
	 * Gets the linux distribution.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @return the linux distribution
	 * @throws DockerException the docker exception
	 */
	public String getLinuxDistribution( byte[] privateKey, String userName, String ipAddress) throws DockerException {
		ArrayList<String> result = new ArrayList<>();
		try {
			result = connection.executeCommand(privateKey, userName, ipAddress, VMConstants.LINUX_DISTRIBUTION);
			if (result.contains(VMConstants.OS_FEDORA)) {
				result.add(VMConstants.OS_FEDORA);
			} else if (result.contains(VMConstants.OS_DEBIAN)) {
				result.add(VMConstants.OS_DEBIAN);
			} else {
				result.add(VMConstants.OS_OTHER);
			}
		} catch (JSchException | IOException e) {
			throw new DockerException(VMConstants.DOCKER_INSTALL_FAILED_MSG, e);
		}
		return result.get(0);
	}

	/**
	 * Install docker.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @param linuxDist the linux dist
	 * @throws DockerException the docker exception
	 */
	public void installDocker(byte[] privateKey, String userName, String ipAddress, String linuxDist) throws DockerException {
		try {
			if (linuxDist.contains(VMConstants.OS_FEDORA)) {
				connection.executeCommand(privateKey, userName, ipAddress, VMConstants.FINSTALL_DOCKER_COMMAND);
			} else if (linuxDist.contains(VMConstants.OS_DEBIAN)) {
				connection.executeCommand(privateKey, userName, ipAddress, VMConstants.DINSTALL_DOCKER_COMMAND);
			}
		} catch (JSchException | IOException e) {
			throw new DockerException(VMConstants.DOCKER_INSTALL_FAILED_MSG, e);
		}
	}

	/**
	 * Start docker service.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @throws DockerException the docker exception
	 */
	public void startDockerService(byte[] privateKey, String userName, String ipAddress) throws DockerException {
		try {
			connection.executeCommand(privateKey, userName, ipAddress, VMConstants.START_DOCKER_SERVICE);
		} catch (JSchException | IOException e) {
			throw new DockerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
	}

	/**
	 * Check for docker.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @return true, if successful
	 * @throws DockerException the docker exception
	 */
	public boolean checkForDocker(byte[] privateKey, String userName, String ipAddress) throws DockerException {
		// Check if docker is installed or not
		ArrayList<String> result = null;
		boolean isDockerInstalled = true;
		try {
			result = connection.executeCommand(privateKey, userName, ipAddress, VMConstants.DOCKER_VERSION);
		} catch (JSchException | IOException e) {
			throw new DockerException(VMConstants.DOCKER_MISSING_MSG, e);
		}
		if (result.isEmpty()) {
			isDockerInstalled = false;
		}
		return isDockerInstalled;
	}

	/**
	 * Check for docker service.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @return true, if successful
	 * @throws DockerException the docker exception
	 */
	public boolean checkForDockerService(byte[] privateKey, String userName, String ipAddress) throws DockerException {
		ArrayList<String> result = null;
		boolean isDockerServiceRunning = true;
		// check is docker service is up or not
		try {
			result = connection.executeCommand(privateKey, userName, ipAddress, VMConstants.DOCKER_STATUS_COMMAND);
		} catch (JSchException | IOException e) {
			throw new DockerException(VMConstants.DOCKER_SERVICE_NOT_RUNNING_MSG, e);
		}
		if (!result.isEmpty() && result.get(0) != null && result.get(0).contains("inactive")) {
			isDockerServiceRunning = false;
		}

		return isDockerServiceRunning;
	}

	/**
	 * *************
	 * Method to get Map of virtual machine system properties
	 * *********************.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @return the VM stats
	 * @throws DockerException the docker exception
	 */
	public Map<String, Integer> getVMStats(byte[] privateKey, String userName, String ipAddress) throws DockerException {
		ArrayList<String> vmStats = new ArrayList<>();
		try {
			vmStats = connection.executeCommand(privateKey, userName, ipAddress, VMConstants.VM_STATS);
		} catch (JSchException | IOException e) {
			throw new DockerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		logger.log(Level.INFO, String.format("Stats Keys %s", vmStats.get(1)));
		logger.log(Level.INFO, String.format("Stats values %s", vmStats.get(2)));
		String[] vmParameters = vmStats.get(1).split(" ");
		String[] vmVal = vmStats.get(2).split(" ");

		List<String> vmParametersFiltered = new ArrayList<>();
		List<String> vmValFiltered = new ArrayList<>();

		Map<String, Integer> vmStatsMap = new HashMap<>();
		for (String val : vmParameters) {
			if (StringUtils.isNotBlank(val)) {
				vmParametersFiltered.add(val);
			}
		}
		for (String val : vmVal) {
			if (StringUtils.isNotBlank(val)) {
				vmValFiltered.add(val);
			}
		}
		logger.log(Level.INFO, String.format("Length of the keys array %s", vmParametersFiltered.size()));
		logger.log(Level.INFO, String.format("Length of the val array %s", vmValFiltered.size()));

		for (int i = 0; i < vmParametersFiltered.size(); i++) {
			vmStatsMap.put(vmParametersFiltered.get(i), Integer.parseInt(vmValFiltered.get(i)));
		}

		return vmStatsMap;
	}
}
