package ie.ncirl.container.manager.library.configurevm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.jcraft.jsch.JSchException;

import ie.ncirl.container.manager.library.configurevm.constants.VMConstants;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;

public class ContainerConfig {
	VMConnection connection = new VMConnection();

	/***************
	 * Method to get Map of Container Stats properties
	 ***********************/
	public Map<String, String> getContainerStats(byte[] privateKey, String userName, String ipAddress, String containerID) throws ContainerException {
		ArrayList<String> containerStats = new ArrayList<>();
		try {
			containerStats = connection.executeCommand(privateKey, userName, ipAddress, String.format(VMConstants.CONTAINER_STATS, containerID));
		} catch (JSchException | IOException e) {
			throw new ContainerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		System.out.println(containerStats.get(0));// Logger INFO
		System.out.println(containerStats.get(1));// Logger INFO
		String[] containerParam = containerStats.get(0).split("  ");
		String[] containerVal = containerStats.get(1).split("  ");

		List<String> containerParamFilt = new ArrayList<>();
		List<String> containerValFilt = new ArrayList<>();
		Map<String, String> containerStatsMap = new HashMap<>();

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
		//System.out.println("Length of the keys array" + containerParamFilt.size()); // Logger INFO
		//System.out.println("Length of the val array" + containerValFilt.size()); // Logger INFO
		for (int i = 0; i < containerParamFilt.size(); i++) {
			containerStatsMap.put(containerParamFilt.get(i), containerValFilt.get(i));
		}

		containerStatsMap.forEach((k, v) -> System.out.println("Key :" + k + "  Value :" + v)); // Logger INFO
		return containerStatsMap;
	}

	public List<String> stopContainers(byte[] privateKey, String userName, String ipAddress, List<String> containerIds) throws ContainerException {
		String containerId = new String();
		List<String> listOfContainersStopped = new ArrayList<>();
		for (String container : containerIds) {
			containerId += " " + container;
		}
		System.out.println(String.format(VMConstants.DOCKER_CONTAINER_STOP, containerId)); // Logger INFO
		try {
			listOfContainersStopped = connection.executeCommand(privateKey, userName, ipAddress, String.format(VMConstants.DOCKER_CONTAINER_STOP, containerId));
		} catch (JSchException | IOException e) {
			throw new ContainerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		return listOfContainersStopped;
	}

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
		containerIds.forEach(s -> System.out.println(s));
		return containerIds;
	}

	/***************
	 * Method to get List of containers Running in a vm
	 ***********************/
	public ArrayList<String> getContainerIds(byte[] privateKey, String userName, String ipAddress) throws ContainerException {
		ArrayList<String> containerIds = new ArrayList<>();
		try {
			containerIds = connection.executeCommand(privateKey, userName, ipAddress, VMConstants.DOCKER_LIST_CONTAINER);
		} catch (JSchException | IOException e) {
			throw new ContainerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		return containerIds;
	}
	/***************
	 * Method to get List of containers Running in a vm by reponame
	 ***********************/
	public ArrayList<String> getContainerIds(byte[] privateKey, String userName, String ipAddress,String repoName) throws ContainerException {
		ArrayList<String> containerIds = new ArrayList<>();
		try {
			containerIds = connection.executeCommand(privateKey, userName, ipAddress, String.format(VMConstants.DOCKER_LIST_WITH_NAME_CONTAINER,repoName));
		} catch (JSchException | IOException e) {
			throw new ContainerException(VMConstants.DOCKER_START_FAILED_MSG, e);
		}
		return containerIds;
	}
	public static void main(String args[]) throws  ContainerException, IOException {
		String keyPath = "D:\\Workspace\\AWS_keypair\\x18180663_keypair.pem";
		String vmUser = "ec2-user";
		String publicIp = "54.154.27.111";
		String repoPath = "anil2993/tomcat";
		Path pkey=Paths.get(keyPath);
		byte[] prvKey=Files.readAllBytes(pkey);
		ContainerConfig config = new ContainerConfig();
		ArrayList<String> containerIDs=null;
		 containerIDs=config.getContainerIds(prvKey,vmUser,publicIp,repoPath);
		 containerIDs.forEach(s -> System.out.println("Container Running :"+s));
		 
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