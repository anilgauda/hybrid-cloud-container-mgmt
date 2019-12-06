package ie.ncirl.container.manager.library.deployer.service.optimizer;

import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.deployer.dto.*;
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
        List<VMData> vms = new ArrayList<>();
        List<ContainerDeploymentData> awsContainers = new ArrayList<>();
        ApplicationData awsApp1 = ApplicationData.builder().memory(500).cpu(50).build();
        ContainerDeploymentData awsApp1Container1 = ContainerDeploymentData.builder().containerId("1")
                .applicationData(awsApp1)
                .build();
        VMData aws = VMData.builder().memory(25 * GB).host("xyz").privateKey(new byte[2])
                .containerDeployments(awsContainers).build();
        awsContainers.add(awsApp1Container1);
        vms.add(aws);

        Optimizer optimizer = new ZigZagOptimizer();
        List<OptimalContainer> actualAllocations = optimizer.getOptimizedContainers(vms, Optimizer.VMOrder.AS_IS);

        List<OptimalContainer> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(
                OptimalContainer.builder()
                        .optimalVMData(aws)
                        .container(buildContainerObject(awsApp1Container1, aws))
                        .build());

        assertThat(actualAllocations, is(expectedAllocations));
    }

    /**
     * INPUT
     * 400,70 500,40 400,30 600,30
     * 600,30 500,40 400,30 400,70
     * <p>
     * OUTPUT
     * 400,70 600,30 -> aws
     * 500,40 400,30 -> azure
     *
     * @throws ContainerException
     */
    @Test
    public void testThatVMsAreOptimized() throws ContainerException {
        List<VMData> vms = new ArrayList<>();
        List<ContainerDeploymentData> awsContainers = new ArrayList<>();

        ApplicationData awsApp1 = ApplicationData.builder().memory(400).cpu(30).build();
        ContainerDeploymentData awsApp1Container = buildContainerDeploymentObject(awsApp1);
        ApplicationData awsApp2 = ApplicationData.builder().memory(400).cpu(70).build();
        ContainerDeploymentData awsApp2Container = buildContainerDeploymentObject(awsApp2);

        awsContainers.add(awsApp1Container);
        awsContainers.add(awsApp2Container);
        VMData aws = buildVMObject("aws", (int) (1.2 * GB), awsContainers);
        vms.add(aws);

        List<ContainerDeploymentData> azureContainers = new ArrayList<>();

        ApplicationData awsApp3 = ApplicationData.builder().memory(600).cpu(30).build();
        ContainerDeploymentData awsApp3Container = buildContainerDeploymentObject(awsApp3);
        ApplicationData awsApp4 = ApplicationData.builder().memory(500).cpu(40).build();
        ContainerDeploymentData awsApp4Container = buildContainerDeploymentObject(awsApp4);

        azureContainers.add(awsApp3Container);
        azureContainers.add(awsApp4Container);
        VMData azure = buildVMObject("azure", (int) (1.2 * GB), azureContainers);
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
        List<VMData> vms = new ArrayList<>();
        List<ContainerDeploymentData> awsContainers = new ArrayList<>();

        ApplicationData awsApp1 = ApplicationData.builder().memory(600).cpu(30).build();
        ContainerDeploymentData awsApp1Container = buildContainerDeploymentObject(awsApp1);
        ApplicationData awsApp2 = ApplicationData.builder().memory(600).cpu(70).build();
        ContainerDeploymentData awsApp2Container = buildContainerDeploymentObject(awsApp2);

        awsContainers.add(awsApp1Container);
        awsContainers.add(awsApp2Container);
        VMData aws = buildVMObject("aws", (int) (1.6 * GB), awsContainers);
        vms.add(aws);

        List<ContainerDeploymentData> azureContainers = new ArrayList<>();

        ApplicationData awsApp3 = ApplicationData.builder().memory(800).cpu(30).build();
        ContainerDeploymentData awsApp3Container = buildContainerDeploymentObject(awsApp3);
        ApplicationData awsApp4 = ApplicationData.builder().memory(500).cpu(40).build();
        ContainerDeploymentData awsApp4Container = buildContainerDeploymentObject(awsApp4);

        azureContainers.add(awsApp3Container);
        azureContainers.add(awsApp4Container);
        VMData azure = buildVMObject("azure", (int) (1.4 * GB), azureContainers);
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
        List<VMData> vms = new ArrayList<>();
        List<ContainerDeploymentData> awsContainers = new ArrayList<>();

        ApplicationData awsApp1 = ApplicationData.builder().memory(600).cpu(30).build();
        ContainerDeploymentData awsApp1Container = buildContainerDeploymentObject(awsApp1);
        ApplicationData awsApp2 = ApplicationData.builder().memory(600).cpu(70).build();
        ContainerDeploymentData awsApp2Container = buildContainerDeploymentObject(awsApp2);

        awsContainers.add(awsApp1Container);
        awsContainers.add(awsApp2Container);
        VMData aws = buildVMObject("aws", (int) (1.6 * GB), awsContainers);
        vms.add(aws);

        List<ContainerDeploymentData> azureContainers = new ArrayList<>();

        ApplicationData awsApp3 = ApplicationData.builder().memory(800).cpu(30).build();
        ContainerDeploymentData awsApp3Container = buildContainerDeploymentObject(awsApp3);
        ApplicationData awsApp4 = ApplicationData.builder().memory(500).cpu(40).build();
        ContainerDeploymentData awsApp4Container = buildContainerDeploymentObject(awsApp4);

        azureContainers.add(awsApp3Container);
        azureContainers.add(awsApp4Container);
        VMData azure = buildVMObject("azure", (int) (1.4 * GB), azureContainers);
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
     * Helper method to generate unique containers from ApplicationData.
     *
     * @param applicationData ApplicationData
     * @return Unique container
     */
    private ContainerDeploymentData buildContainerDeploymentObject(ApplicationData applicationData) {
        return ContainerDeploymentData.builder()
                .containerId(UUID.randomUUID().toString())
                .applicationData(applicationData)
                .build();
    }

    /**
     * Helper method to generate unique VMData from memory and containers.
     *
     * @param memory               memory of VMData
     * @param containerDeployments containers present in VMData
     * @return Unique VMData
     */
    private VMData buildVMObject(String host, Integer memory, List<ContainerDeploymentData> containerDeployments) {
        return VMData.builder()
                .memory(memory).host(host)
                .privateKey(new byte[2])
                .containerDeployments(containerDeployments).build();
    }

    /**
     * Helper method to build Container object
     *
     * @param containerDeploymentData ContainerDeploymentData existing in VMData
     * @param vmData                  VMData
     * @return Container
     */
    private Container buildContainerObject(ContainerDeploymentData containerDeploymentData, VMData vmData) {
        return Container.builder()
                .id(containerDeploymentData.getContainerId())
                .applicationData(containerDeploymentData.getApplicationData())
                .server(vmData)
                .cpu(containerDeploymentData.getApplicationData().getCpu())
                .memory(containerDeploymentData.getApplicationData().getMemory())
                .build();
    }

    /**
     * Create an OptimalContainer object
     *
     * @param container Container
     * @param vmData        Virtual Machine
     * @return OptimalContainer
     */
    private OptimalContainer buildOptimalContainerObject(Container container, VMData vmData) {
        return OptimalContainer.builder()
                .container(container)
                .optimalVMData(vmData)
                .build();

    }
}