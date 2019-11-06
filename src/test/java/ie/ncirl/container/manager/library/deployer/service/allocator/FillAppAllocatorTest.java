package ie.ncirl.container.manager.library.deployer.service.allocator;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class FillAppAllocatorTest {

    private int GB = 1000; // In MB

    private List<VM> vms = new ArrayList<>();
    private VM aws = VM.builder().id(1L).host("aws.com").memory(GB).build();
    private VM azure = VM.builder().id(2L).host("azure.com").memory(GB).build();

    private Application app = Application.builder().name("app1").registryImageUrl("reg1").memory(500).build();

    @Before
    public void setup() {
        vms.add(aws);
        vms.add(azure);
    }

    @Test
    public void testShouldAllocateAppInVM() {
        int numDeployments = 2;

        AppAllocatorStrategy allocator = new FillAppAllocator();
        List<Allocation> actualAllocations = allocator.getAllocationData(app, numDeployments, vms).getAllocations();

        List<Allocation> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(Allocation.builder().application(app).count(numDeployments).vm(aws).build());

        assertThat(actualAllocations, is(expectedAllocations));
    }

    @Test
    public void testShouldAllocateTwoInFirstAndOneInSecondVM() {
        int numDeployments = 3;

        AppAllocatorStrategy allocator = new FillAppAllocator();
        List<Allocation> actualAllocations = allocator.getAllocationData(app, numDeployments, vms).getAllocations();

        List<Allocation> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(Allocation.builder().application(app).count(2).vm(aws).build());
        expectedAllocations.add(Allocation.builder().application(app).count(1).vm(azure).build());

        assertThat(actualAllocations, is(expectedAllocations));
    }

    @Test
    public void testShouldHaveFailedAllocations() {
        int numDeployments = 5;

        AppAllocator allocator = new AppAllocator();
        AppAllocatorStrategy fillStrategy = new FillAppAllocator();
        allocator.setAppAllocatorStrategy(fillStrategy);
        Integer failedAllocations = allocator.getAllocations(app, numDeployments, vms).getFailedAllocations();

        assertThat(failedAllocations, greaterThan(0));
    }

}