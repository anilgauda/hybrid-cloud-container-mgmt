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

/**
 * The Class VMConnection.
 */
public class VMConnection {
	
	/** The connection. */
	private static VMConnection connection = new VMConnection();

	/**
	 * Instantiates a new VM connection.
	 */
	private VMConnection() {

	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public static VMConnection getConnection() {
		return connection;

	}

	/**
	 * Execute command.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @param command the command
	 * @return the array list
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JSchException the j sch exception
	 */
	@SuppressWarnings({ "unused", "static-access" })
	ArrayList<String> executeCommand(byte[] privateKey, String userName, String ipAddress, String command) throws IOException, JSchException {
		String line = null;
		ArrayList<String> result = new ArrayList<>();
		JSch javaShell = new JSch();
		// javaShell.addIdentity(privateKeyPath);

		javaShell.addIdentity("", privateKey, null, null);
		javaShell.setConfig(VMConstants.HOST_KEY_CHECK_CONFIG, VMConstants.HOST_KEY_CHECK_CONFIG_VALUE);
		Session session = javaShell.getSession(userName, ipAddress, VMConstants.SSH_PORT);
		try {
			session.connect(60000);
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
}
