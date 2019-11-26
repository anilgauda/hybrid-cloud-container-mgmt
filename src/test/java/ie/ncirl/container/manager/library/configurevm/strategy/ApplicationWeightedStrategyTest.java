package ie.ncirl.container.manager.library.configurevm.strategy;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ie.ncirl.container.manager.library.configurevm.model.Application;
import ie.ncirl.container.manager.library.configurevm.model.Container;
import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;
import ie.ncirl.container.manager.library.configurevm.model.VM;

public class ApplicationWeightedStrategyTest {
	@Test
	public void testDeploy() {
		ApplicationWeightedStrategy appTest=new ApplicationWeightedStrategy();
		List<DeploymentModel> deploymentList=new ArrayList<>();
		for(int i=0;i<5;i++) {
			DeploymentModel dp=new DeploymentModel();
				Container container=new Container();
				Application app=new Application();
				app.setName("app1");
				app.setRegistryImageUrl("repo1");
				container.setApplication(app);
				
				VM vm=new VM();
				vm.setName("vm1");
				container.setServer(vm);
				
				VM optimalVM=new VM();
				vm.setName("vm2");
				
				dp.setContainer(container);
				dp.setOptimalVM(optimalVM);
				deploymentList.add(dp);
		}
		appTest.deploy(deploymentList, 50);
	}
}
