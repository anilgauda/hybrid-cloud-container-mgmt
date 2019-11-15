package ie.ncirl.container.manager.library.deployer.dto;

import ie.ncirl.container.manager.app.util.KeyUtils;
import ie.ncirl.container.manager.common.domain.Application;
import ie.ncirl.container.manager.common.domain.VM;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Server {
    private String host;

    private String username;

    @ToString.Exclude
    private String privateKey;

    private Integer availableMemory;

    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    public Server(VM vm) {
        this.host = vm.getHost();
        this.privateKey = KeyUtils.inString(vm.getPrivateKey());
        this.username = vm.getUsername();
    }
}
