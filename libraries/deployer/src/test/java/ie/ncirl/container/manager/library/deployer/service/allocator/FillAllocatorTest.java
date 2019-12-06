package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.dto.ApplicationData;
import ie.ncirl.container.manager.library.deployer.dto.VMData;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FillAllocatorTest {

    private int GB = 1000; // In MB

    private List<VMData> servers = new ArrayList<>();
    private VMData aws = VMData.builder().host("aws.com").memory(GB).build();
    private VMData azure = VMData.builder().host("azure.com").memory(GB).build();

    private ApplicationData app = ApplicationData.builder().name("app1").registryImageUrl("reg1").memory(500).build();

    @Before
    public void setup() {
        servers.add(aws);
        servers.add(azure);
    }

    @Test
    public void testShouldAllocateAppInVM() {
        int numDeployments = 2;

        AppAllocatorStrategy allocator = new FillAllocator();
        List<Allocation> actualAllocations = allocator.getAllocationData(app, numDeployments, servers).getAllocations();

        List<Allocation> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(Allocation.builder().applicationData(app).count(numDeployments).server(aws).build());

        assertThat(actualAllocations, is(expectedAllocations));
    }

    @Test
    public void testShouldAllocateTwoInFirstAndOneInSecondVM() {
        int numDeployments = 3;

        AppAllocatorStrategy allocator = new FillAllocator();
        List<Allocation> actualAllocations = allocator.getAllocationData(app, numDeployments, servers).getAllocations();

        List<Allocation> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(Allocation.builder().applicationData(app).count(2).server(aws).build());
        expectedAllocations.add(Allocation.builder().applicationData(app).count(1).server(azure).build());

        assertThat(actualAllocations, is(expectedAllocations));
    }

    @Test
    public void testShouldHaveFailedAllocations() {
        int numDeployments = 5;

        AppAllocator allocator = new AppAllocator();
        AppAllocatorStrategy fillStrategy = new FillAllocator();
        allocator.setAppAllocatorStrategy(fillStrategy);
        int failedAllocations = allocator.getAllocations(app, numDeployments, servers).getFailedAllocations();

        assertTrue(failedAllocations > 0);
    }

}