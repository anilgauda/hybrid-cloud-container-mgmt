package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.dto.AllocationData;
import ie.ncirl.container.manager.library.deployer.dto.Server;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SpreadAllocatorTest {

    private int GB = 1000; // In MB

    private List<Server> servers = new ArrayList<>();
    private Server aws = Server.builder().host("aws.com").availableMemory(GB).build();
    private Server azure = Server.builder().host("azure.com").availableMemory(GB).build();

    private Application app = Application.builder().name("app1").registryImageUrl("reg1").memory(400).build();

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
        expectedAllocations.add(Allocation.builder().application(app).count(1).server(aws).build());

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
        expectedAllocations.add(Allocation.builder().application(app).count(1).server(aws).build());
        expectedAllocations.add(Allocation.builder().application(app).count(1).server(azure).build());

        assertThat(actualAllocations, is(expectedAllocations));
        assertThat(allocationData.getFailedAllocations(), equalTo(2));
    }

    @Test
    public void testShouldNotAllocateWithSameExistingApp() {
        int numDeployments = 2;

        List<Application> applications = new ArrayList<>();
        applications.add(app);
        aws.setApplications(applications);
        AppAllocator allocator = new AppAllocator();
        allocator.setAppAllocatorStrategy(new SpreadAllocator());

        AllocationData allocationData = allocator.getAllocations(app, numDeployments, servers);
        List<Allocation> actualAllocations = allocationData.getAllocations();

        List<Allocation> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(Allocation.builder().application(app).count(1).server(azure).build());

        assertThat(actualAllocations, is(expectedAllocations));
        assertThat(allocationData.getFailedAllocations(), equalTo(1));
    }
}