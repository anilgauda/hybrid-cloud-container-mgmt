package ie.ncirl.container.manager.library.configurevm;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import ie.ncirl.container.manager.library.configurevm.constants.VMConstants;
import ie.ncirl.container.manager.library.configurevm.exception.DockerInstallationException;

public class ConfigureVMTest {
	
	public static final String USERNAME="mock";
	public static final String PRIVATEKEYPATH="D:\\Workspace\\AWS_keypair\\x18180663_keypair.pem";
	public static final String IP_ADDRESS="127.0.0.1"; 
	public static final int PORT=22;
	public final static String MODE_OF_EXECUTION = "exec";
	public final static String OS_FEDORA = "fedora";
	
	private JSch jsch=mock(JSch.class);
	private Session session=mock(Session.class);
	private Channel channel=mock(Channel.class);
	private ChannelExec channelExec=mock(ChannelExec.class);
	 
	@Before
	public void setUp() throws JSchException {
		jsch.addIdentity(PRIVATEKEYPATH);
		jsch.setConfig(VMConstants.HOST_KEY_CHECK_CONFIG, VMConstants.HOST_KEY_CHECK_CONFIG_VALUE);
		when(jsch.getSession(USERNAME, IP_ADDRESS, PORT)).thenReturn(session);
		when(session.openChannel(MODE_OF_EXECUTION)).thenReturn(channel);
	}
	@Test
	public void testGetLinuxDistribution() throws DockerInstallationException, IOException, JSchException {
	ConfigureVM config=new ConfigureVM();
	
	
	
	when(jsch.getSession(USERNAME, IP_ADDRESS, PORT)).thenReturn(session);
	when(session.openChannel(MODE_OF_EXECUTION)).thenReturn(channel);
	ArrayList<String> dist=config.getLinuxDistribution(PRIVATEKEYPATH, USERNAME, IP_ADDRESS);
	
	BufferedReader br=mock(BufferedReader.class);
	when(br.readLine()).thenReturn(OS_FEDORA);
	System.out.println(dist.get(0));
}
}
