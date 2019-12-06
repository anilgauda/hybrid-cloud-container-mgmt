package ie.ncirl.container.manager.library.configurevm.strategy;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import ie.ncirl.container.manager.library.configurevm.model.ApplicationModel;
import ie.ncirl.container.manager.library.configurevm.model.Container;
import ie.ncirl.container.manager.library.configurevm.model.ContainersList;
import ie.ncirl.container.manager.library.configurevm.model.DeploymentModel;
import ie.ncirl.container.manager.library.configurevm.model.VMModel;

public class VmWeightedStrategyTest {
	VMWeightedStrategy vmTest = new VMWeightedStrategy();

	@Test
	public void testDeploy() {
		List<DeploymentModel> deploymentList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			DeploymentModel dp = new DeploymentModel();
			Container container = new Container();
			ApplicationModel app = new ApplicationModel();
			app.setName("app1");
			app.setRegistryImageUrl("repo1");
			container.setApplication(app);

			VMModel vm = new VMModel();
			vm.setName("vm1");
			container.setServer(vm);

			VMModel optimalVM = new VMModel();
			vm.setName("vm2");

			dp.setContainer(container);
			dp.setOptimalVM(optimalVM);
			deploymentList.add(dp);
		}
		ContainersList containerList = vmTest.deploy(deploymentList, 50);
		Assert.assertNotNull(containerList);
	}

	@Test
	public void testDeployedContainers() {
		List<DeploymentModel> deploymentList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			DeploymentModel dp = new DeploymentModel();
			Container container = new Container();
			ApplicationModel app = new ApplicationModel();
			app.setName("app1");
			app.setRegistryImageUrl("repo1");
			container.setApplication(app);

			VMModel vm = new VMModel();
			vm.setName("vm1");
			container.setServer(vm);

			VMModel optimalVM = new VMModel();
			vm.setName("vm2");

			dp.setContainer(container);
			dp.setOptimalVM(optimalVM);
			deploymentList.add(dp);
		}
		ContainersList containerList = vmTest.deploy(deploymentList, 50);
		Assert.assertEquals(10, containerList.getDeployedContainers().size());
	}

	@Test
	public void testUndeployedContainers() {
		List<DeploymentModel> deploymentList = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			DeploymentModel dp = new DeploymentModel();
			Container container = new Container();
			ApplicationModel app = new ApplicationModel();
			app.setName("app1");
			app.setRegistryImageUrl("repo1");
			container.setApplication(app);

			VMModel vm = new VMModel();
			vm.setName("vm1");
			container.setServer(vm);

			VMModel optimalVM = new VMModel();
			vm.setName("vm2");

			dp.setContainer(container);
			dp.setOptimalVM(optimalVM);
			deploymentList.add(dp);
		}
		ContainersList containerList = vmTest.deploy(deploymentList, 50);
		Assert.assertEquals(20, containerList.getUndeployedContainers().size());

	}
}
