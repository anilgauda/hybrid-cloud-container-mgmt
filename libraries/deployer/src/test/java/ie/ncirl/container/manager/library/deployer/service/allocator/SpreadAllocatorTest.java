package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.library.deployer.dto.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SpreadAllocatorTest {

    private int GB = 1000; // In MB

    private List<VMData> servers = new ArrayList<>();
    private VMData aws = VMData.builder().host("aws.com").memory(GB).build();
    private VMData azure = VMData.builder().host("azure.com").memory(GB).build();

    private ApplicationData app = ApplicationData.builder().name("app1").registryImageUrl("reg1").memory(400).build();

    @Before
    public void setup() {
        servers.add(aws);
        servers.add(azure);
    }

    @Test
    public void testShouldAllocateAppInAWS() {
        int numDeployments = 1;

        AppAllocator allocator = new AppAllocator();
        allocator.setAppAllocatorStrategy(new SpreadAllocator());

        List<Allocation> actualAllocations = allocator.getAllocations(app, numDeployments, servers).getAllocations();

        List<Allocation> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(Allocation.builder().applicationData(app).count(1).server(aws).build());

        assertThat(actualAllocations, is(expectedAllocations));
    }

    @Test
    public void testShouldAllocateAppOnlyOncePerServer() {
        int numDeployments = 4;

        AppAllocator allocator = new AppAllocator();
        allocator.setAppAllocatorStrategy(new SpreadAllocator());

        AllocationData allocationData = allocator.getAllocations(app, numDeployments, servers);
        List<Allocation> actualAllocations = allocationData.getAllocations();

        List<Allocation> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(Allocation.builder().applicationData(app).count(1).server(aws).build());
        expectedAllocations.add(Allocation.builder().applicationData(app).count(1).server(azure).build());

        assertThat(actualAllocations, is(expectedAllocations));
        assertThat(allocationData.getFailedAllocations(), equalTo(2));
    }

    @Test
    public void testShouldNotAllocateWithSameExistingApp() {
        int numDeployments = 2;

        List<ContainerDeploymentData> containerDeployments = new ArrayList<>();
        containerDeployments.add(ContainerDeploymentData.builder().applicationData(app).build());
        aws.setContainerDeployments(containerDeployments);
        AppAllocator allocator = new AppAllocator();
        allocator.setAppAllocatorStrategy(new SpreadAllocator());

        AllocationData allocationData = allocator.getAllocations(app, numDeployments, servers);
        List<Allocation> actualAllocations = allocationData.getAllocations();

        List<Allocation> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(Allocation.builder().applicationData(app).count(1).server(azure).build());

        assertThat(actualAllocations, is(expectedAllocations));
        assertThat(allocationData.getFailedAllocations(), equalTo(1));
    }
}