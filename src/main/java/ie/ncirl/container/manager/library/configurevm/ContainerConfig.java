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
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;

/**
 * The Class ContainerConfig.
 *
 * @author Anil
 */

public class ContainerConfig {
	
	/** The connection. */
	VMConnection connection=VMConnection.getConnection();
	Logger logger= Logger.getLogger(ContainerConfig.class.getName());
	
	/**
	 * *************
	 * Method to get Map of Container Stats properties
	 * *********************.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @param containerID the container ID
	 * @return the container stats
	 * @throws ContainerException the container exception
	 */
	public Map<String, String> getContainerStats(byte[] privateKey, String userName, String ipAddress, String containerID) throws ContainerException {
		ArrayList<String> containerStats = new ArrayList<>();
		Map<String, String> containerStatsMap = new HashMap<>();
		List<String> containerParamFilt = new ArrayList<>();
		List<String> containerValFilt = new ArrayList<>();

		try {
			containerStats = connection.executeCommand(privateKey, userName, ipAddress, String.format(VMConstants.CONTAINER_STATS, containerID));
		} catch (JSchException | IOException e) {
			throw new ContainerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		if(!containerStats.isEmpty()) {
		String[] containerParam = containerStats.get(0).split("  ");
		String[] containerVal = containerStats.get(1).split("  ");

		

		for (String val : containerParam) {
			if (StringUtils.isNotBlank(val)) {
				containerParamFilt.add(val);
			}
		}
		for (String val : containerVal) {
			if (StringUtils.isNotBlank(val)) {
				containerValFilt.add(val);
			}
		}
		for (int i = 0; i < containerParamFilt.size(); i++) {
			containerStatsMap.put(containerParamFilt.get(i), containerValFilt.get(i));
		}

		containerStatsMap.forEach((k, v) -> logger.log(Level.INFO,"Key :" + k + "  Value :" + v)); // Logger INFO
		}
		return containerStatsMap;
	}

	/**
	 * Stop containers.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @param containerIds the container ids
	 * @return the list
	 * @throws ContainerException the container exception
	 */
	public List<String> stopContainers(byte[] privateKey, String userName, String ipAddress, List<String> containerIds) throws ContainerException {
		String containerId = new String();
		List<String> listOfContainersStopped = new ArrayList<>();
		for (String container : containerIds) {
			containerId += " " + container;
		}
		logger.log(Level.INFO,String.format(VMConstants.DOCKER_CONTAINER_STOP, containerId)); // Logger INFO
		try {
			listOfContainersStopped = connection.executeCommand(privateKey, userName, ipAddress, String.format(VMConstants.DOCKER_CONTAINER_STOP, containerId));
		} catch (JSchException | IOException e) {
			throw new ContainerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		return listOfContainersStopped;
	}

	/**
	 * Start containers.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @param repoPath the repo path
	 * @return the list
	 * @throws ContainerException the container exception
	 */
	public List<String> startContainers(byte[] privateKey, String userName, String ipAddress, String repoPath) throws ContainerException {
		List<String> containerIds = new ArrayList<>();
		try {
			containerIds = connection.executeCommand(privateKey, userName, ipAddress, String.format(VMConstants.DOCKER_CONTAINER_START, repoPath));
		} catch (JSchException | IOException e) {
			throw new ContainerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		for (int i = 0; i < containerIds.size(); i++) {
			String containerid = containerIds.get(i);
			if (StringUtils.isNotEmpty(containerid)) {
				containerIds.set(i, containerid.substring(0, 12));
			}
		}
		containerIds.forEach(s ->logger.log(Level.INFO,s));
		return containerIds;
	}

	/**
	 * *************
	 * Method to get List of containers Running in a vm
	 * *********************.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @return the container ids
	 * @throws ContainerException the container exception
	 */
	/**
	 * @param privateKey
	 * @param userName
	 * @param ipAddress
	 * @return
	 * @throws ContainerException
	 */
	public ArrayList<String> getContainerIds(byte[] privateKey, String userName, String ipAddress) throws ContainerException {
		ArrayList<String> containerIds = new ArrayList<>();
		try {
			containerIds = connection.executeCommand(privateKey, userName, ipAddress, VMConstants.DOCKER_LIST_CONTAINER);
		} catch (JSchException | IOException e) {
			throw new ContainerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		return containerIds;
	}
	
	/**
	 * *************
	 * Method to get List of containers Running in a vm by reponame
	 * *********************.
	 *
	 * @param privateKey the private key
	 * @param userName the user name
	 * @param ipAddress the ip address
	 * @param repoName the repo name
	 * @return the container ids
	 * @throws ContainerException the container exception
	 */
	public ArrayList<String> getContainerIds(byte[] privateKey, String userName, String ipAddress,String repoName) throws ContainerException {
		ArrayList<String> containerIds = new ArrayList<>();
		try {
			containerIds = connection.executeCommand(privateKey, userName, ipAddress, String.format(VMConstants.DOCKER_LIST_WITH_NAME_CONTAINER,repoName));
		} catch (JSchException | IOException e) {
			throw new ContainerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		return containerIds;
	}
}
