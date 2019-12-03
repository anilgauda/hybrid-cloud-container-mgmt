package ie.ncirl.container.manager.library.deployer.service.optimizer;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.deployer.dto.Container;
import ie.ncirl.container.manager.library.deployer.dto.OptimalContainer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ZigZagOptimizerTest {

    private int GB = 1000; // In MB


    @Test
    public void testThatAllContainersAreAllocated() throws ContainerException {
        List<VM> vms = new ArrayList<>();
        List<ContainerDeployment> awsContainers = new ArrayList<>();
        Application awsApp1 = Application.builder().memory(500).cpu(50).build();
        ContainerDeployment awsApp1Container1 = ContainerDeployment.builder().containerId("1")
                .application(awsApp1)
                .build();
        VM aws = VM.builder().memory(25 * GB).host("xyz").privateKey(new byte[2])
                .containerDeployments(awsContainers).build();
        awsContainers.add(awsApp1Container1);
        vms.add(aws);

        Optimizer optimizer = new ZigZagOptimizer();
        List<OptimalContainer> actualAllocations = optimizer.getOptimizedContainers(vms, Optimizer.VMOrder.AS_IS);

        List<OptimalContainer> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(
                OptimalContainer.builder()
                        .optimalVM(aws)
                        .container(buildContainerObject(awsApp1Container1, aws))
                        .build());

        assertThat(actualAllocations, is(expectedAllocations));
    }

    /**
     * INPUT
     * 400,70 500,40 400,30 600,30
     * 600,30 500,40 400,30 400,70
     *
     * OUTPUT
     * 400,70 600,30 -> aws
     * 500,40 400,30 -> azure
     * @throws ContainerException
     */
    @Test
    public void testThatVMsAreOptimized() throws ContainerException {
        List<VM> vms = new ArrayList<>();
        List<ContainerDeployment> awsContainers = new ArrayList<>();

        Application awsApp1 = Application.builder().memory(400).cpu(30).build();
        ContainerDeployment awsApp1Container = buildContainerDeploymentObject(awsApp1);
        Application awsApp2 = Application.builder().memory(400).cpu(70).build();
        ContainerDeployment awsApp2Container = buildContainerDeploymentObject(awsApp2);

        awsContainers.add(awsApp1Container);
        awsContainers.add(awsApp2Container);
        VM aws = buildVMObject("aws", (int) (1.2 * GB), awsContainers);
        vms.add(aws);

        List<ContainerDeployment> azureContainers = new ArrayList<>();

        Application awsApp3 = Application.builder().memory(600).cpu(30).build();
        ContainerDeployment awsApp3Container = buildContainerDeploymentObject(awsApp3);
        Application awsApp4 = Application.builder().memory(500).cpu(40).build();
        ContainerDeployment awsApp4Container = buildContainerDeploymentObject(awsApp4);

        azureContainers.add(awsApp3Container);
        azureContainers.add(awsApp4Container);
        VM azure = buildVMObject("azure", (int) (1.2 * GB), azureContainers);
        vms.add(azure);

        Optimizer optimizer = new ZigZagOptimizer();
        List<OptimalContainer> actualContainers = optimizer.getOptimizedContainers(vms, Optimizer.VMOrder.AS_IS);

        List<OptimalContainer> expectedContainers = new ArrayList<>();
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp2Container, aws), aws));
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp3Container, azure), aws));
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp4Container, azure), azure));
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp1Container, aws), azure));

        assertThat(actualContainers, is(expectedContainers));
    }

    @Test
    public void testThatVMsAreOptimizedInAscOrder() throws ContainerException {
        List<VM> vms = new ArrayList<>();
        List<ContainerDeployment> awsContainers = new ArrayList<>();

        Application awsApp1 = Application.builder().memory(600).cpu(30).build();
        ContainerDeployment awsApp1Container = buildContainerDeploymentObject(awsApp1);
        Application awsApp2 = Application.builder().memory(600).cpu(70).build();
        ContainerDeployment awsApp2Container = buildContainerDeploymentObject(awsApp2);

        awsContainers.add(awsApp1Container);
        awsContainers.add(awsApp2Container);
        VM aws = buildVMObject("aws", (int) (1.6 * GB), awsContainers);
        vms.add(aws);

        List<ContainerDeployment> azureContainers = new ArrayList<>();

        Application awsApp3 = Application.builder().memory(800).cpu(30).build();
        ContainerDeployment awsApp3Container = buildContainerDeploymentObject(awsApp3);
        Application awsApp4 = Application.builder().memory(500).cpu(40).build();
        ContainerDeployment awsApp4Container = buildContainerDeploymentObject(awsApp4);

        azureContainers.add(awsApp3Container);
        azureContainers.add(awsApp4Container);
        VM azure = buildVMObject("azure", (int) (1.4 * GB), azureContainers);
        vms.add(azure);

        Optimizer optimizer = new ZigZagOptimizer();
        List<OptimalContainer> actualContainers = optimizer.getOptimizedContainers(vms, Optimizer.VMOrder.ASC_MEM);

        List<OptimalContainer> expectedContainers = new ArrayList<>();
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp2Container, aws), azure));
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp1Container, aws), azure));
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp4Container, azure), aws));
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp3Container, azure), aws));

        assertThat(actualContainers, is(expectedContainers));
    }

    @Test
    public void testThatVMsAreOptimizedInDescOrder() throws ContainerException {
        List<VM> vms = new ArrayList<>();
        List<ContainerDeployment> awsContainers = new ArrayList<>();

        Application awsApp1 = Application.builder().memory(600).cpu(30).build();
        ContainerDeployment awsApp1Container = buildContainerDeploymentObject(awsApp1);
        Application awsApp2 = Application.builder().memory(600).cpu(70).build();
        ContainerDeployment awsApp2Container = buildContainerDeploymentObject(awsApp2);

        awsContainers.add(awsApp1Container);
        awsContainers.add(awsApp2Container);
        VM aws = buildVMObject("aws", (int) (1.6 * GB), awsContainers);
        vms.add(aws);

        List<ContainerDeployment> azureContainers = new ArrayList<>();

        Application awsApp3 = Application.builder().memory(800).cpu(30).build();
        ContainerDeployment awsApp3Container = buildContainerDeploymentObject(awsApp3);
        Application awsApp4 = Application.builder().memory(500).cpu(40).build();
        ContainerDeployment awsApp4Container = buildContainerDeploymentObject(awsApp4);

        azureContainers.add(awsApp3Container);
        azureContainers.add(awsApp4Container);
        VM azure = buildVMObject("azure", (int) (1.4 * GB), azureContainers);
        vms.add(azure);

        Optimizer optimizer = new ZigZagOptimizer();
        List<OptimalContainer> actualContainers = optimizer.getOptimizedContainers(vms, Optimizer.VMOrder.DESC_MEM);

        List<OptimalContainer> expectedContainers = new ArrayList<>();
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp2Container, aws), aws));
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp3Container, azure), aws));
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp4Container, azure), azure));
        expectedContainers.add(buildOptimalContainerObject(buildContainerObject(awsApp1Container, aws), azure));

        assertThat(actualContainers, is(expectedContainers));
    }

    /**
     * Helper method to generate unique containers from Application.
     *
     * @param application Application
     * @return Unique container
     */
    private ContainerDeployment buildContainerDeploymentObject(Application application) {
        return ContainerDeployment.builder()
                .containerId(UUID.randomUUID().toString())
                .application(application)
                .build();
    }

    /**
     * Helper method to generate unique VM from memory and containers.
     *
     * @param memory               memory of VM
     * @param containerDeployments containers present in VM
     * @return Unique VM
     */
    private VM buildVMObject(String host, Integer memory, List<ContainerDeployment> containerDeployments) {
        return VM.builder()
                .memory(memory).host(host)
                .privateKey(new byte[2])
                .containerDeployments(containerDeployments).build();
    }

    /**
     * Helper method to build Container object
     *
     * @param containerDeployment ContainerDeployment existing in VM
     * @param vm                  VM
     * @return Container
     */
    private Container buildContainerObject(ContainerDeployment containerDeployment, VM vm) {
        return Container.builder()
                .id(containerDeployment.getContainerId())
                .application(containerDeployment.getApplication())
                .server(vm)
                .cpu(containerDeployment.getApplication().getCpu())
                .memory(containerDeployment.getApplication().getMemory())
                .build();
    }

    /**
     * Create an OptimalContainer object
     *
     * @param container Container
     * @param vm        Virtual Machine
     * @return OptimalContainer
     */
    private OptimalContainer buildOptimalContainerObject(Container container, VM vm) {
        return OptimalContainer.builder()
                .container(container)
                .optimalVM(vm)
                .build();

    }
}