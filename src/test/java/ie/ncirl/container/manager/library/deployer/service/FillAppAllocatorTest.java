package ie.ncirl.container.manager.library.deployer.service;

import ie.ncirl.container.manager.common.domain.Application;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FillAppAllocatorTest {

    public void testGetAllocations() {
        Application app = Application.builder().name("app1").registryImageUrl("reg").build();
        int numDeployments = 3;
        List<String> keys = new ArrayList<>();


    }

}