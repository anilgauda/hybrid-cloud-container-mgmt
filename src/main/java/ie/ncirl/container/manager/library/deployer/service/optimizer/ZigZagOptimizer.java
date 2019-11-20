package ie.ncirl.container.manager.library.deployer.service.optimizer;

import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.ContainerConfig;
import ie.ncirl.container.manager.library.configurevm.constants.ContainerConstants;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.deployer.dto.Container;
import ie.ncirl.container.manager.library.deployer.dto.OptimalContainer;

import java.util.*;
import java.util.stream.Collectors;

/*
  Strategy working:
  Consider virtual servers with pair of cpu, mem
  V1 -> 4,5 4,5 4,7 \4,7 -> 18,24
  V2 -> 6,4 6,4 5,4 5,4 -> 22,18
  V3 -> \6,2 4,4 6,2 4,4 -> 20,12
  V4 -> \2,6 3,4 \2,6 3,4 \10,1 -> 20,19
  10,1 6,2
  2,6 2,6 4,7 4,7
  Here, some servers are fully utilized and some are partially utilized.
  After running the algorithm,
  V1 -> 10,1 2,6 2,6 6,2 4,7 -> 24,22   25,25
  V2 ->
  Above, all servers will be optimized with both cpu and memory
 */


/**
 * Example
 * AWS -> 400,30 400,70   800,100
 * Azure -> 600,30 500,40   1100,70
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


public class ZigZagOptimizer implements Optimizer {

    @Override
    public List<OptimalContainer> getOptimalContainerData(List<VM> vms) throws ContainerException {
        List<OptimalContainer> optimalContainers = new ArrayList<>();

        setApplicationResourceConsumption(vms);
        List<Container> containers = getAllContainersInVMs(vms);

        if (containers.size() == 0) return optimalContainers;

        List<Container> cpuSortedContainers = getContainersSortedByCPU(containers);
        List<Container> memSortedContainers = getContainersSortedByMemory(containers);

        for (VM vm : vms) {
            int usedCpu = 0;
            int usedMemory = 1; //initially we want some value to be greater than the other
            int availableCpu = 100;
            int availableMemory = vm.getMemory();

            // Sometimes a container may be not available but we still have to check the other list
            // in such a case, we force to pick the other list. If both lists are forced that means
            // no fitting containers are available, in this case, we move to the next VM
            boolean forcePickCpuList = false;
            boolean forcePickMemList = false;

            while (usedMemory < availableMemory && usedCpu < availableCpu &&
                    cpuSortedContainers.size() + memSortedContainers.size() > 0) {
                if (forcePickCpuList && forcePickMemList) break;

                List<Container> containerListToPick = cpuSortedContainers;
                int percentUsedMemory = (usedMemory / availableMemory) * 100;
                if ((forcePickMemList || usedCpu > percentUsedMemory) && !forcePickCpuList) {
                    containerListToPick = memSortedContainers;
                }

                Optional<Container> maybeContainer = getFirstFittingContainer(
                        containerListToPick, availableMemory, availableCpu);

                if (!maybeContainer.isPresent()) {
                    // == is intentional, we are comparing references not value
                    if (containerListToPick == cpuSortedContainers) {
                        forcePickMemList = true;
                    } else {
                        forcePickCpuList = true;
                    }
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

        return optimalContainers;
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
                container -> container.getCpu() < cpu && container.getMemory() < memory
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

    /**
     * Creates a Container object with Application, memory and cpu in every VM
     *
     * @param vms all VMs to scan
     * @return container list
     */
    private List<Container> getAllContainersInVMs(List<VM> vms) {
        return vms.stream().flatMap(
                vm -> vm.getContainerDeployments().stream().map(
                        containerDeployment -> Container.builder()
                                .id(containerDeployment.getContainerId())
                                .application(containerDeployment.getApplication())
                                .server(vm)
                                .cpu(containerDeployment.getApplication().getCpu())
                                .memory(containerDeployment.getApplication().getMemory())
                                .build())
        ).collect(Collectors.toList());
    }


    /**
     * Gets container stats for those application that do not have cpu/mem set
     *
     * @param vms VMs
     */
    public void setApplicationResourceConsumption(List<VM> vms) throws ContainerException {
        ContainerConfig config = new ContainerConfig();

        for (VM vm : vms) {
            for (ContainerDeployment container : vm.getContainerDeployments()) {
                Application application = container.getApplication();

                if (application.getMemory() != 0 && application.getCpu() != 0) continue;

                Map<String, String> stats = config.getContainerStats(vm.getPrivateKey(), vm.getUsername(), vm.getHost(), container.getContainerId());

                if (application.getMemory() == 0) {
                    application.setMemory(getMemoryFromStats(
                            stats.get(ContainerConstants.CONTAINER_STATS_KEY_MEMORY_USAGE)));
                }

                if (application.getCpu() == 0) {
                    application.setCpu(getCpuFromStats(
                            stats.get(ContainerConstants.CONTAINER_STATS_KEY_CPU_USAGE)));
                }
            }
        }
    }

    /**
     * Get memory stat from docker stats
     *
     * @param memStat memory in MB
     * @return memory int
     */
    private Integer getMemoryFromStats(String memStat) {
        return (int) Math.ceil(Double.parseDouble(memStat.replace("MiB", "")));
    }

    /**
     * Get CPU stat
     *
     * @param cpuStat cpu usage in %
     * @return cpu int
     */
    private Integer getCpuFromStats(String cpuStat) {
        return (int) Math.ceil(Double.parseDouble(cpuStat.replace("%", "")));
    }

}
