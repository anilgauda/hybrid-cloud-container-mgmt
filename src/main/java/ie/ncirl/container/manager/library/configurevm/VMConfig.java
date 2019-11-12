package ie.ncirl.container.manager.library.configurevm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.JSchException;

import ie.ncirl.container.manager.library.configurevm.constants.VMConstants;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;

public class VMConfig {
	VMConnection connection=new VMConnection();

	public ArrayList<String> getLinuxDistribution( byte[] privateKey, String userName, String ipAddress) throws DockerException {
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
		return result;
	}

	public void installDocker(byte[] privateKey, String userName, String ipAddress, String linuxDist) throws DockerException {
		try {
			if (linuxDist.equalsIgnoreCase(VMConstants.OS_FEDORA)) {
				connection.executeCommand(privateKey, userName, ipAddress, VMConstants.FINSTALL_DOCKER_COMMAND);
			} else if (linuxDist.equalsIgnoreCase(VMConstants.OS_DEBIAN)) {
				connection.executeCommand(privateKey, userName, ipAddress, VMConstants.DINSTALL_DOCKER_COMMAND);
			}
		} catch (JSchException | IOException e) {
			throw new DockerException(VMConstants.DOCKER_INSTALL_FAILED_MSG, e);
		}
	}

	public void startDockerService(byte[] privateKey, String userName, String ipAddress) throws DockerException {
		try {
			connection.executeCommand(privateKey, userName, ipAddress, VMConstants.START_DOCKER_SERVICE);
		} catch (JSchException | IOException e) {
			throw new DockerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
	}

	public boolean checkForDocker(byte[] privateKey, String userName, String ipAddress) throws DockerException {
		// Check if docker is installed or not
		ArrayList<String> result = null;
		boolean isDockerInstalled = true;
		try {
			result = connection.executeCommand(privateKey, userName, ipAddress, VMConstants.DOCKER_VERSION);
		} catch (JSchException | IOException e) {
			throw new DockerException(VMConstants.DOCKER_MISSING_MSG, e);
		}
		if (result.get(0) != null && !result.get(0).contains("Docker version")) {// Need to use Regex for matching
			isDockerInstalled = false;
		}
		return isDockerInstalled;
	}

	public boolean checkForDockerService(byte[] privateKey, String userName, String ipAddress) throws DockerException {
		ArrayList<String> result = null;
		boolean isDockerServiceRunning = true;
		// check is docker service is up or not
		try {
			result = connection.executeCommand(privateKey, userName, ipAddress, VMConstants.DOCKER_STATUS_COMMAND);
		} catch (JSchException | IOException e) {
			throw new DockerException(VMConstants.DOCKER_SERVICE_NOT_RUNNING_MSG, e);
		}
		if (result.get(0) != null && result.get(0).contains("inactive")) {// Need to use Regex for matching
			isDockerServiceRunning = false;
		}

		return isDockerServiceRunning;
	}

	/***************
	 * Method to get Map of virtual machine system properties
	 ***********************/
	public Map<String, Integer> getVMStats(byte[] privateKey, String userName, String ipAddress) throws DockerException {
		ArrayList<String> vmStats = new ArrayList<>();
		try {
			vmStats = connection.executeCommand(privateKey, userName, ipAddress, VMConstants.VM_STATS);
		} catch (JSchException | IOException e) {
			throw new DockerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		System.out.println(vmStats.get(1));// Logger INFO
		System.out.println(vmStats.get(2));// Logger INFO
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
		System.out.println("Length of the keys array" + vmParametersFiltered.size()); // Logger INFO
		System.out.println("Length of the val array" + vmValFiltered.size()); // Logger INFO
		for (int i = 0; i < vmParametersFiltered.size(); i++) {
			vmStatsMap.put(vmParametersFiltered.get(i), Integer.parseInt(vmValFiltered.get(i)));
		}

		vmStatsMap.forEach((k, v) -> System.out.println("Key :" + k + "  Value :" + v)); // Logger INFO
		return vmStatsMap;
	}

	public static void main(String args[]) throws IOException, JSchException, DockerException {
		String keyPath = "D:\\Workspace\\AWS_keypair\\x18180663_keypair.pem";
		String vmUser = "ec2-user";
		String publicIp = "54.154.27.111";
		String repoPath = "anil2993/tomcat";
		Path pkey=Paths.get(keyPath);
		byte[] prvKey=Files.readAllBytes(pkey);
		VMConfig config = new VMConfig();
		ArrayList<String> containerIDs=null;
		
		  String linuxDist =
		  config.getLinuxDistribution(prvKey,vmUser,publicIp).get(0);
		  System.out.println(Arrays.toString(prvKey));
		 System.out.println("Current Linux Dist is :" + linuxDist); 
		 
		 /*boolean
		 isDockerInstalled=config.checkForDocker(keyPath, vmUser, publicIp); boolean
		  isDockerStarted=config.checkForDockerService(keyPath, vmUser, publicIp);
		  if(!isDockerInstalled) { config.installDocker(keyPath, vmUser, publicIp,
		  linuxDist); } if(!isDockerStarted) { config.startDockerService(keyPath,
		  vmUser, linuxDist); }
		
		config.startContainers(keyPath, vmUser, publicIp, repoPath);
		containerIDs=config.getContainerIds(keyPath, vmUser, publicIp);
		containerIDs.forEach(s -> System.out.println("Container : "+s+" is running"));*/
	}
}
