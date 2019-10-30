package ie.ncirl.container.manager.library.deployer.service;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.deployer.dto.Allocation;
import ie.ncirl.container.manager.library.deployer.service.allocator.AppAllocator;
import ie.ncirl.container.manager.library.deployer.service.allocator.FillAppAllocator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FillAppAllocatorTest {

    private List<VM> vms = new ArrayList<>();
    private VM aws = VM.builder().id(1L).host("aws.com").build();
    private VM azure = VM.builder().id(2L).host("azure.com").build();

    private Application app1 = Application.builder().name("app1").registryImageUrl("reg1").build();
    private Application app2 = Application.builder().name("app1").registryImageUrl("reg2").build();


    @Before
    public void setup() {
        vms.add(aws);
        vms.add(azure);
    }

    @Test
    public void testGetAllocationsOneInEachVM() {
        int numDeployments = 2;

        AppAllocator allocator = new FillAppAllocator();
        List<Allocation> actualAllocations = allocator.getAllocations(app1, numDeployments, vms);

        List<Allocation> expectedAllocations = new ArrayList<>();
        expectedAllocations.add(Allocation.builder().application(app1).vm(aws).build());
        expectedAllocations.add(Allocation.builder().application(app1).vm(azure).build());

        assertThat(actualAllocations, is(expectedAllocations));
    }

}