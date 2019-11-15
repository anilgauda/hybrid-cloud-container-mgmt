package ie.ncirl.container.manager.library.deployer.service.optimizer;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.configurevm.exception.DockerException;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import ie.ncirl.container.manager.library.deployer.dto.Server;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ZigZagOptimizerTest {

    private int GB = 1000; // In MB


    @Test
    public void testThatAllContainersAreAllocated() throws DockerException, ContainerException {
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
        AllocationData allocationData = optimizer.getAllocationData(vms);
        List<Allocation> actualAllocations = allocationData.getAllocations();

        List<Allocation> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(
                Allocation.builder()
                        .server(new Server(aws))
                        .application(awsApp1)
                        .count(1)
                        .build());

        assertThat(actualAllocations, is(expectedAllocations));
    }

    @Test
    public void testThatVMsAreOptimized() throws DockerException, ContainerException {
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
        AllocationData allocationData = optimizer.getAllocationData(vms);
        List<Allocation> actualAllocations = allocationData.getAllocations();

        List<Allocation> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(buildAllocationObject(aws, awsApp2));
        expectedAllocations.add(buildAllocationObject(aws, awsApp3));
        expectedAllocations.add(buildAllocationObject(azure, awsApp4));
        expectedAllocations.add(buildAllocationObject(azure, awsApp1));

        assertThat(actualAllocations, is(expectedAllocations));
    }

    /**
     * Creates an Allocation object for allocating a container in a VM
     *
     * @param vm          VM where the container will be allocated
     * @param application The application to which the container belongs
     * @return Allocation object
     */
    private Allocation buildAllocationObject(VM vm, Application application) {
        return Allocation.builder()
                .server(new Server(vm))
                .application(application)
                .count(1)
                .build();
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

}