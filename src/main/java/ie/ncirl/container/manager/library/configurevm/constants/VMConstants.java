package ie.ncirl.container.manager.library.configurevm.constants;

public class VMConstants {
    public final static String HOST_KEY_CHECK_CONFIG = "StrictHostKeyChecking";
    public final static String HOST_KEY_CHECK_CONFIG_VALUE = "no";
    public final static int SSH_PORT = 22;
    public final static String MODE_OF_EXECUTION = "exec";
    public final static String OS_FEDORA = "fedora";
    public final static String OS_DEBIAN = "debian";
    public final static String OS_OTHER = "other";

    public final static String LINUX_DISTRIBUTION = "cat /etc/os-release | grep ID_LIKE";
    /******** Install and configure Docker Commands for Debian based OS *******************/
    public final static String DINSTALL_DOCKER_COMMAND = "sudo apt-get install docker-engine -y";
    public final static String START_DOCKER_SERVICE = "sudo service docker start";

    /******** Install and configure Docker Commands for Fedora based OS *******************/
    public final static String FINSTALL_DOCKER_COMMAND = "sudo yum install docker-engine -y";
/******** Get Docker containers based on image name processes  *******************/
	//Right now it is hello-world
	public final static String DOCKER_PROCESSES_COMMAND="sudo docker ps -aqf ancestor=hello-world";
	
	/***** Get Docker Status in the Linux environement ********/
	public final static String DOCKER_VERSION="docker -v";
	
	/***** Get Docker Status in the Linux environement ********/
	public final static String DOCKER_STATUS_COMMAND="sudo service docker status | grep Active";
	
	/**** List of docker containers running******/
	public final static String DOCKER_LIST_CONTAINER="docker container ls -q";
	
	/***** List of docker containers with image name *****/
	public final static String DOCKER_LIST_WITH_NAME_CONTAINER="docker container ls -f ancestor=%s -q";
	
	/******** Get Vm stats*****/
	public final static String VM_STATS="vmstat";
    public final static String VM_STAT_FREE_MEMORY = ":free";

    /******************Get Containers Stats ******************/
	public final static String CONTAINER_STATS="docker stats %s --no-stream";
	
	/***************Docker Container stop******************/
	public final static String DOCKER_CONTAINER_STOP="docker container stop %s";
	
	/************** Docker Command to run docker in detacted mode*********/
	public final static String DOCKER_CONTAINER_START="docker container run -d %s";
    /******************************Exception Messages*************************************/
    public final static String DOCKER_INSTALL_FAILED_MSG = "Docker Installation Failed";
    public final static String DOCKER_START_FAILED_MSG = "Failed to Start Docker Service";
    public final static String DOCKER_MISSING_MSG = "Docker is not Installed";
    public final static String DOCKER_SERVICE_NOT_RUNNING_MSG = "Docker Service is not running in VM";

}
