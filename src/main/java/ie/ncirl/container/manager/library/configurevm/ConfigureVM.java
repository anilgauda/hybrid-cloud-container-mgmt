package ie.ncirl.container.manager.library.configurevm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import ie.ncirl.container.manager.library.configurevm.constants.VMConstants;
import ie.ncirl.container.manager.library.configurevm.exception.DockerInstallationException;

public class ConfigureVM {
	@SuppressWarnings({ "unused", "static-access" })
	private ArrayList<String> executeCommand(String privateKeyPath, String userName, String ipAddress, String command) throws IOException, JSchException {
		String line = null;
		ArrayList<String> result = new ArrayList<>();
		JSch javaShell = new JSch();
		javaShell.addIdentity(privateKeyPath);
		javaShell.setConfig(VMConstants.HOST_KEY_CHECK_CONFIG, VMConstants.HOST_KEY_CHECK_CONFIG_VALUE);
		Session session = javaShell.getSession(userName, ipAddress, VMConstants.SSH_PORT);
		try {
			session.connect();
		} catch (JSchException sessionException) {
			/** Close Connection when there is error while connecting to a session **/
			session.disconnect();
			throw sessionException;
		}
		if (session.isConnected()) {
			/** Creating channel to remotely execute the linux commands **/
			ChannelExec channel = (ChannelExec) session.openChannel(VMConstants.MODE_OF_EXECUTION);
			channel.setCommand(command);
			try {
				channel.connect();
			} catch (JSchException channelException) {
				/** Close Connection when there is error while opening a channel **/
				channel.disconnect();
				throw channelException;
			}
			if (channel.isConnected()) {
				InputStreamReader in = new InputStreamReader(channel.getInputStream());
				BufferedReader br = new BufferedReader(in);
				while ((line = br.readLine()) != null) {
					result.add(line);
				}
				channel.disconnect();
			}
			session.disconnect();
		}
		return result;
	}

	public ArrayList<String> getLinuxDistribution(String privateKeyPath, String userName, String ipAddress) throws DockerInstallationException {
		ArrayList<String> result = new ArrayList<>();
		try {
			result = executeCommand(privateKeyPath, userName, ipAddress, VMConstants.LINUX_DISTRIBUTION);
			if (result.contains(VMConstants.OS_FEDORA)) {
				result.add(VMConstants.OS_FEDORA);
			} else if (result.contains(VMConstants.OS_DEBIAN)) {
				result.add(VMConstants.OS_DEBIAN);
			} else {
				result.add(VMConstants.OS_OTHER);
			}
		} catch (JSchException | IOException e) {
			throw new DockerInstallationException(VMConstants.DOCKER_INSTALL_FAILED_MSG, e);
		}
		return result;
	}

	public void configureEnvironment(String privateKeyPath, String userName, String ipAddress, String linuxDist) throws DockerInstallationException {
		try {
			if (linuxDist.equalsIgnoreCase(VMConstants.OS_FEDORA)) {
				executeCommand(privateKeyPath, userName, ipAddress, VMConstants.FINSTALL_DOCKER_COMMAND);
			} else if (linuxDist.equalsIgnoreCase(VMConstants.OS_DEBIAN)) {
				executeCommand(privateKeyPath, userName, ipAddress, VMConstants.DINSTALL_DOCKER_COMMAND);
			}
		} catch (JSchException | IOException e) {
			throw new DockerInstallationException(VMConstants.DOCKER_INSTALL_FAILED_MSG, e);
		}
		try {
			executeCommand(privateKeyPath, userName, ipAddress, VMConstants.START_DOCKER_SERVICE);
		} catch (JSchException | IOException e) {
			throw new DockerInstallationException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
	}

	public boolean checkForDocker(String privateKeyPath, String userName, String ipAddress) throws DockerInstallationException {
		// Check if docker is installed or not
		ArrayList<String> result = null;
		boolean isDockerInstalled = true;
		try {
			result = executeCommand(privateKeyPath, userName, ipAddress, VMConstants.DOCKER_VERSION);
		} catch (JSchException | IOException e) {
			throw new DockerInstallationException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		if (result.get(0) != null && !result.contains("Docker version")) {// Need to use Regex for matching
			isDockerInstalled = false;
		}
		return isDockerInstalled;
	}

	public boolean checkForDockerService(String privateKeyPath, String userName, String ipAddress) throws DockerInstallationException {
		ArrayList<String> result = null;
		boolean isDockerServiceRunning = true;
		// check is docker service is up or not
		try {
			result = executeCommand(privateKeyPath, userName, ipAddress, VMConstants.DOCKER_STATUS_COMMAND);
		} catch (JSchException | IOException e) {
			throw new DockerInstallationException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		if (result.get(0) != null && result.contains("inactive")) {// Need to use Regex for matching
			isDockerServiceRunning = false;
		}

		return isDockerServiceRunning;
	}

	public ArrayList<String> getContainerIds(String privateKeyPath, String userName, String ipAddress) throws DockerInstallationException {
		ArrayList<String> containerIds = new ArrayList<>();
		try {
			containerIds = executeCommand(privateKeyPath, userName, ipAddress, VMConstants.DOCKER_LIST_CONTAINER);
		} catch (JSchException | IOException e) {
			throw new DockerInstallationException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		return containerIds;
	}

	public static void main(String args[]) throws DockerInstallationException, IOException, JSchException {
		String linuxDis = null;
		ConfigureVM config = new ConfigureVM();
		config.getContainerIds("D:\\Workspace\\AWS_keypair\\x18180663_keypair.pem", "ec2-user", "52.214.70.41");
		config.checkForDocker("D:\\Workspace\\AWS_keypair\\x18180663_keypair.pem", "ec2-user", "52.214.70.41");
	}
}
