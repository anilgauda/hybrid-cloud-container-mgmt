package ie.ncirl.container.manager.common.domain.logging;

import java.time.LocalTime;

import ie.ncirl.container.manager.common.domain.Application;


public class ApplicationLogs implements Log {
	@Override
	public String createLogData(Object obj,String operation, String currentUser,String userRole) {
		Application appObj=(Application)obj;
		String logString=String.format("[%s] :User: %s with Role: %s has performed %s on Application %s", LocalTime.now(),currentUser,userRole,operation,appObj.getName());
		return logString;
	}

}
