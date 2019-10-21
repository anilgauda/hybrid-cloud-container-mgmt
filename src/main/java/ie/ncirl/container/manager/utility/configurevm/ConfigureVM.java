package ie.ncirl.container.manager.utility.configurevm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import ie.ncirl.container.manager.utility.configurevm.constants.VMConstants;
import ie.ncirl.container.manager.utility.configurevm.exception.DockerInstallationException;

public class ConfigureVM {
	@SuppressWarnings({ "unused", "static-access" })
	private String executeCommand(String privateKeyPath, String userName, String ipAddress, String command)
			throws IOException, JSchException {
		String line = null;
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
				BufferedReader reader = new BufferedReader(new InputStreamReader(channel.getInputStream()));
				line = reader.readLine();
				channel.disconnect();
			}
			session.disconnect();
		}
		return line;
	}

	public String getLinuxDistribution(String privateKeyPath, String userName, String ipAddress)
			throws DockerInstallationException {
		String result = null;
		try {
			result = executeCommand(privateKeyPath, userName, ipAddress, VMConstants.LINUX_DISTRIBUTION);
			if (result.contains(VMConstants.OS_FEDORA)) {
				result = VMConstants.OS_FEDORA;
			} else if (result.contains(VMConstants.OS_DEBIAN)) {
				result = VMConstants.OS_DEBIAN;
			} else {
				result = VMConstants.OS_OTHER;
			}
		} catch (JSchException | IOException e) {
			throw new DockerInstallationException(VMConstants.DOCKER_INSTALL_FAILED_MSG, e);
		}
		return result;
	}

	public void configureEnvironment(String privateKeyPath, String userName, String ipAddress, String linuxDist)
			throws DockerInstallationException {
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

	public static void main(String args[]) throws DockerInstallationException, IOException, JSchException {
		String linuxDis = null;
		ConfigureVM config = new ConfigureVM();
		// config.getLinuxDistribution("D:\\Workspace\\AWS_keypair\\azure_keypair.pem",
		// "azure-user", "23.97.230.56");
		linuxDis = config.getLinuxDistribution("D:\\Workspace\\AWS_keypair\\x18180663_keypair.pem", "ec2-user",
				"34.252.173.121");
		if (linuxDis.equals(VMConstants.OS_OTHER)) {
			System.out.println("This Application do not support provided version of os");
		} else {
			config.configureEnvironment("D:\\Workspace\\AWS_keypair\\x18180663_keypair.pem", "ec2-user",
					"34.252.173.121", linuxDis);
		}
	}
}
