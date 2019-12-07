package ie.ncirl.container.manager.library.deployer.service.optimizer;

import ie.ncirl.container.manager.library.deployer.dto.Container;
import ie.ncirl.container.manager.library.deployer.dto.OptimalContainer;
import ie.ncirl.container.manager.library.deployer.dto.VMData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


/**
 * How it works?
 * Consider following four servers with Memory,CPU pairs of docker containers running in each VMData
 * Example:
 * AWS 1  -> 400,30 400,70  -- Total: 800, 100
 * AWS 2  -> 300,30 400,60  -- Total: 700, 90
 * Azure  -> 600,30 500,40  -- Total: 1100,70
 * IBM    -> 900,10 300,30  -- Total: 1200,40
 *
 * For sake of simplicity of this example, assume above servers have max memory of 1200 and max cpu of 100.
 *
 * The algorithm makes use of the idea that memory and cpu are separate components so
 * an applicationData with high cpu can go with an applicationData with high memory thus optimizing
 * the VMData. Hence, if we fill a VMData with container having the highest CPU until the CPU used is
 * greater than memory then fill with highest memory until the memory used is greater than CPU
 * and go on repeating this process until the VMData is completely filled, we will have an optimized VMData.
 * We can then run this for all VMs in the list. There are certain edge cases that have been handled
 * in the code and commented below.
 *
 *
 * 600,30 600,30 500,40 500,40 400,30 400,60 400,70 300,30 -> memSorted
 * 400,70 400,60 500,40 500,40 300,30 400,30 600,30 600,30 -> cpuSorted
 *
 * AWS1  -> 400,70 600,30 -> 1000,100
 * AWS2  -> 400,60 600,30 -> 1000,90
 * Azure -> 500,40 500,40 -> 1000,80
 * IBM   -> 300,30 400,30  -> 700 60
 *
 * As you can see, IBM now uses comparatively less  memory and cpu, we can possibly switch this
 * instance to a lower priced instance. If they are more containers and vms in real case scenarios,
 * it is very likely to end up with free/empty vms that we can directly shut down effectively saving money.
 */


public class ZigZagOptimizer extends OptimizerTemplate {

    public void findOptimalContainers(List<VMData> vms, List<Container> containers, List<OptimalContainer> optimalContainers) {

        List<Container> cpuSortedContainers = getContainersSortedByCPU(containers);
        List<Container> memSortedContainers = getContainersSortedByMemory(containers);

        for (VMData vmData : vms) {
            int usedCpu = 0;
            int usedMemory = 1; //initially we want some value to be greater than the other
            int availableCpu = 100;
            int availableMemory = vmData.getMemory();

            // Sometimes a container may be not available but we still have to check the other list
            // in such a case, we force to pick the other list. If both lists are forced that means
            // no fitting containers are available, in this case, we move to the next VM
            // Note: This is also necessary to prevent an infinite loop in cases where it's not possible
            // to optimize VMs 100%. In this case we break the loop after checking both lists
            boolean forcePickCpuList = false;
            boolean forcePickMemList = false;

            while (usedMemory < availableMemory && usedCpu < availableCpu &&
                    cpuSortedContainers.size() + memSortedContainers.size() > 0) {
                if (forcePickCpuList && forcePickMemList) break;

                List<Container> containerListToPick = cpuSortedContainers;
                int percentUsedMemory = (int)((float) usedMemory / availableMemory) * 100; // to make memory comparable we get %
                if ((forcePickMemList || usedCpu > percentUsedMemory) && !forcePickCpuList) {
                    containerListToPick = memSortedContainers;
                }

                int memoryLeft = availableMemory - usedMemory;
                int cpuLeft = availableCpu - usedCpu;
                Optional<Container> maybeContainer = getFirstFittingContainer(
                        containerListToPick, memoryLeft, cpuLeft);

                if (!maybeContainer.isPresent()) {
                    // == is intentional, we are comparing references not value
                    if (containerListToPick == cpuSortedContainers) {
                        forcePickMemList = true;
                    } else {
                        forcePickCpuList = true;
                    }
                    // No more space left in VMData
                    continue;
                }
                Container container = maybeContainer.get();
                cpuSortedContainers.remove(container); // can look for improvements since this is O(n)
                memSortedContainers.remove(container); // maybe use a mark and sweep like algorithm

                OptimalContainer optimalContainer = OptimalContainer.builder()
                        .optimalVMData(vmData)
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
     *
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
     * @param containers Containers in VMData
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
     * @param containers Containers in VMData
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
}
