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


    /******************************Exception Messages*************************************/
    public final static String DOCKER_INSTALL_FAILED_MSG = "Docker Installation Failed";
    public final static String DOCKER_START_FAILED_MSG = "Failed to Start Docker Service";

}
