package ie.ncirl.container.manager.app.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VMDTO implements DTO {

    private Long id;

    @NotNull(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Please enter host")
    private String host;

    @NotNull
    private String username;

    @NotNull
    @ToString.Exclude
    private String privateKey;

    @NotNull(message = "Please select a provider")
    private Long providerId;
}
