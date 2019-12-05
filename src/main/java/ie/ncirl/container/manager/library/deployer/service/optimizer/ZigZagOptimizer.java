package ie.ncirl.container.manager.library.deployer.service.optimizer;

import ie.ncirl.container.manager.app.util.CryptUtil;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.deployer.dto.Container;
import ie.ncirl.container.manager.library.deployer.dto.OptimalContainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


/**
 * How it works?
 * Consider following four servers with Memory,CPU pairs of docker containers running in each VM
 * Example:
 * AWS 1  -> 400,30 400,70  -- Total: 800, 100
 * AWS 2  -> 300,30 400,60  -- Total: 700, 90
 * Azure  -> 600,30 500,40  -- Total: 1100,70
 * IBM    -> 900,10 300,30  -- Total: 1200,40
 * <p>
 * For sake of simplicity of this example, assume above servers have max memory of 1200 and max cpu of 100.
 * <p>
 * The algorithm makes use of the idea that memory and cpu are separate components so
 * an application with high cpu can go with an application with high memory thus optimizing
 * the VM. Hence, if we fill a VM with container having the highest CPU until the CPU used is
 * greater than memory then fill with highest memory until the memory used is greater than CPU
 * and go on repeating this process until the VM is completely filled, we will have an optimized VM.
 * We can then run this for all VMs in the list. There are certain edge cases that have been handled
 * in the code and commented below.
 *
 *
 * <p>
 * 600,30 500,40 400,30 400,70 -> memSorted
 * 400,70 500,40 400,30 600,30 -> cpuSorted
 * <p>
 * AWS   -> 600,40 400,70 -> 1000,110
 * Azure -> 500,40 400,30 -> 900,80
 * <p>
 * AWS   -> 400,70 600,30  -> 1000 100
 * Azure ->
 */


public class ZigZagOptimizer extends OptimizerTemplate {

    public void findOptimalContainers(List<VM> vms, List<Container> containers, List<OptimalContainer> optimalContainers) {

        List<Container> cpuSortedContainers = getContainersSortedByCPU(containers);
        List<Container> memSortedContainers = getContainersSortedByMemory(containers);

        for (VM vm : vms) {
            int usedCpu = 0;
            int usedMemory = 1; //initially we want some value to be greater than the other
            int availableCpu = 100;
            int availableMemory = vm.getMemory();

            while (usedMemory < availableMemory && usedCpu < availableCpu &&
                    cpuSortedContainers.size() + memSortedContainers.size() > 0) {

                List<Container> containerListToPick = cpuSortedContainers;
                int percentUsedMemory = (usedMemory / availableMemory) * 100; // to make memory comparable we get %
                if (usedCpu > percentUsedMemory) {
                    containerListToPick = memSortedContainers;
                }

                int memoryLeft = availableMemory - usedMemory;
                int cpuLeft = availableCpu - usedCpu;
                Optional<Container> maybeContainer = getFirstFittingContainer(
                        containerListToPick, memoryLeft, cpuLeft);

                if (!maybeContainer.isPresent()) {
                    // No more space left in VM
                    continue;
                }
                Container container = maybeContainer.get();
                cpuSortedContainers.remove(container); // can look for improvements since this is O(n)
                memSortedContainers.remove(container); // maybe use a mark and sweep like algorithm

                OptimalContainer optimalContainer = OptimalContainer.builder()
                        .optimalVM(vm)
                        .container(container)
                        .build();
                optimalContainers.add(optimalContainer);
                usedCpu += container.getCpu();
                usedMemory += container.getMemory();
            }
        }
    }


    /**
     * Filters the list of containers and gets the first container that is able to fit with given cpu/memory constraints
     * @param containers Containers to search in
     * @param memory     memory constraint
     * @param cpu        cpu constraint
     * @return the first matching container
     */
    private Optional<Container> getFirstFittingContainer(List<Container> containers, Integer memory, Integer cpu) {
        return containers.stream().filter(
                container -> container.getCpu() <= cpu && container.getMemory() <= memory
        ).findFirst();
    }

    /**
     * Gets containers with ascending cpu and descending memory
     *
     * @param containers Containers in VM
     * @return containers sorted
     */
    private List<Container> getContainersSortedByCPU(List<Container> containers) {
        List<Container> cpuSortedContainers = new ArrayList<>(containers);
        cpuSortedContainers.sort(
                Comparator.comparing(Container::getCpu, Comparator.reverseOrder())
                        .thenComparing(Container::getMemory)
        );
        return cpuSortedContainers;
    }

    /**
     * Gets containers with ascending memory and descending cpu
     *
     * @param containers Containers in VM
     * @return containers sorted
     */
    private List<Container> getContainersSortedByMemory(List<Container> containers) {
        List<Container> cpuSortedContainers = new ArrayList<>(containers);
        cpuSortedContainers.sort(
                Comparator.comparing(Container::getMemory, Comparator.reverseOrder())
                        .thenComparing(Container::getCpu)
        );
        return cpuSortedContainers;
    }

    public ZigZagOptimizer(CryptUtil cryptUtil) {
        super(cryptUtil);
    }
}
