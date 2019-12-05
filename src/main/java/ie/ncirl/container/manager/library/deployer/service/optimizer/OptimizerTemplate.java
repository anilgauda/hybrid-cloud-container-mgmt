package ie.ncirl.container.manager.library.deployer.service.optimizer;

import ie.ncirl.container.manager.app.util.CryptUtil;
import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.ContainerDeployment;
import ie.ncirl.container.manager.common.domain.VM;
import ie.ncirl.container.manager.library.configurevm.ContainerConfig;
import ie.ncirl.container.manager.library.configurevm.constants.ContainerConstants;
import ie.ncirl.container.manager.library.configurevm.exception.ContainerException;
import ie.ncirl.container.manager.library.deployer.dto.Container;
import ie.ncirl.container.manager.library.deployer.dto.OptimalContainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Uses Template pattern to separate all the common components of an optimization algorithm.
 * Ref: https://sourcemaking.com/design_patterns/template_method
 * Any new optimization algorithm will only have to implement the abstract method findOptimalContainers
 * The rest of the grunt work is handled by this abstract class thus avoiding any code duplication
 */
public abstract class OptimizerTemplate implements Optimizer {

    /**
     * Encryption was added later so the vm private keys passed here are all encrypted.
     * Instead of changing every single variable to use a decrypted version of the private key,
     * we instead use constructor injection to inject a dependency to decrypt it.
     */
    private CryptUtil cryptUtil;

    /**
     * The actual algorithm outline. It calls the implementation of the optimization in findOptimalContainers
     *
     * @param vms     VMs to optimize
     * @param vmOrder The order in which the VMs will be optimized
     * @return Optimal Containers
     * @throws ContainerException exception when jsch fails
     */
    @Override
    public List<OptimalContainer> getOptimizedContainers(List<VM> vms, VMOrder vmOrder) throws ContainerException {
        List<OptimalContainer> optimalContainers = new ArrayList<>();

        setApplicationResourceConsumption(vms);

        List<Container> containers = getAllContainersInVMs(vms);
        if (containers.size() == 0) return optimalContainers;

        switch (vmOrder) {
            case ASC_MEM:
                vms.sort(Comparator.comparing(VM::getMemory));
                break;
            case DESC_MEM:
                vms.sort(Comparator.comparing(VM::getMemory, Comparator.reverseOrder()));
                break;
        }

        findOptimalContainers(vms, containers, optimalContainers);

        return optimalContainers;
    }

    /**
     * The actual implementation of the optimization algorithm. Please refer the class extending this to know
     * the optimization algorithm
     *
     * @param vms               VMs to optimize
     * @param containers        Containers present in VMs
     * @param optimalContainers Optimal Container mapping
     */
    abstract void findOptimalContainers(List<VM> vms, List<Container> containers, List<OptimalContainer> optimalContainers);

    /**
     * Gets container stats for those application that do not have cpu/mem set
     *
     * @param vms VMs
     */
    @Override
    public void setApplicationResourceConsumption(List<VM> vms) throws ContainerException {
        ContainerConfig config = new ContainerConfig();

        for (VM vm : vms) {
            for (ContainerDeployment container : vm.getContainerDeployments()) {
                Application application = container.getApplication();

                if (application.getMemory() != 0 && application.getCpu() != 0) continue;

                Map<String, String> stats = config.getContainerStats(
                        KeyUtils.inBytes(cryptUtil.decryptBytes(vm.getPrivateKey())), vm.getUsername(),
                        vm.getHost(), container.getContainerId());

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


    OptimizerTemplate(CryptUtil cryptUtil) {
        this.cryptUtil = cryptUtil;
    }

}
