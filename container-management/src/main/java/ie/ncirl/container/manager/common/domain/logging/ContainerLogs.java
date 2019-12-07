package ie.ncirl.container.manager.common.domain.logging;

import java.time.LocalTime;

import ie.ncirl.container.manager.common.domain.ContainerDeployment;

public class ContainerLogs implements Log{

	@Override
	public String createLogData(Object obj, String operation, String currentUser, String userRole) {
		ContainerDeployment containerObj=(ContainerDeployment)obj;
		String logString=String.format("[%s] :User: %s with Role: %s has performed %s on Contianer %s",LocalTime.now(), currentUser,userRole,operation,containerObj.getContainerId());
		return logString;
	}

}
