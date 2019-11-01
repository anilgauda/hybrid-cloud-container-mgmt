package ie.ncirl.container.manager.app.dto;

import java.util.Map;

public class Application {
	private String containerId;
	private Map<String, String> containerStats;

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public Map<String, String> getContainerStats() {
		return containerStats;
	}

	public void setContainerStats(Map<String, String> containerStats) {
		this.containerStats = containerStats;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Application [containerId=");
		builder.append(containerId);
		builder.append(", containerStats=");
		builder.append(containerStats);
		builder.append("]");
		return builder.toString();
	}

}
