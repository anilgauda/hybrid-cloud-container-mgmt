package ie.ncirl.container.manager.common.domain.logging;

import java.time.LocalTime;

import ie.ncirl.container.manager.common.domain.VM;

public class VmLogs implements Log{

	@Override
	public String createLogData(Object obj, String operation, String currentUser, String userRole) {
		VM vmObj=(VM)obj;
		String logString=String.format("[%s] : User: %s with Role: %s has performed %s on VM : %s",LocalTime.now(), currentUser,userRole,operation,vmObj.getName());
		return logString;
	}

}
